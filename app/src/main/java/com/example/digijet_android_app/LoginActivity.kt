package com.example.digijet_android_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.widget.CheckBox
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)
        val backButton: Button = findViewById(R.id.backButton)
        val rememberMeCheckBox: CheckBox = findViewById(R.id.rememberMeCheckBox)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val rememberMe = sharedPreferences.getBoolean("rememberMe", false)
        if (rememberMe) {
            val savedEmail = sharedPreferences.getString("email", null)
            val savedPassword = sharedPreferences.getString("password", null)
            if (!savedEmail.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
                emailEditText.setText(savedEmail)
                passwordEditText.setText(savedPassword)
                login(savedEmail, savedPassword)
            }
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val rememberMe = rememberMeCheckBox.isChecked

            if (rememberMe) {
                val editor = sharedPreferences.edit()
                editor.putString("email", email)
                editor.putString("password", password)
                editor.putBoolean("rememberMe", true)
                editor.apply()
            }

            login(email, password)
        }

        backButton.setOnClickListener {
            // Переадресация на экран WelcomeActivity
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "loginWithEmail:success")
                    // Здесь можно перейти на другую активити после успешного входа

                    val editor = sharedPreferences.edit()
                    editor.putString("email", email)
                    editor.putString("password", password)
                    editor.putBoolean("rememberMe", true)
                    editor.apply()

                    // Вызов функции getUserParams в корутине
                    GlobalScope.launch {
                        val nickname = getUserParamByEmail(email, "nickname")
                        val userId = getUserParamByEmail(email, "userId")

                        // Сохранение полученных значений
                        withContext(Dispatchers.Main) {
                            editor.putString("nickname", nickname)
                            editor.putString("userId", userId)
                            editor.apply()

                            val savedNickname = sharedPreferences.getString("nickname", null)
                            val savedUserId = sharedPreferences.getString("userId", null)
                            if (savedNickname != null) {
                                Log.d("nickname", savedNickname)
                            }
                            if (savedUserId != null) {
                                Log.d("userId", savedUserId)
                            }

                            val intent = Intent(this@LoginActivity, MainPageActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    Log.w(TAG, "loginWithEmail:failure", task.exception)
                    // Здесь можно обработать ошибку входа
                }
            }
    }


    private suspend fun getUserParamByEmail(email: String, param: String): String? = withContext(Dispatchers.IO) {
        val firestore = FirebaseFirestore.getInstance()
        val querySnapshot = firestore.collection("users")
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .await()

        if (querySnapshot.isEmpty) {
            // Если запрос не вернул результатов, значит пользователя с таким email нет
            return@withContext null
        }

        // Получаем первый документ из результатов запроса
        val userDocument = querySnapshot.documents.first()

        // Возвращаем значение поля "userId" из документа
        return@withContext userDocument.getString(param)
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
