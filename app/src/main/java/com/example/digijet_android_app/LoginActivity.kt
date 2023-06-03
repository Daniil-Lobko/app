package com.example.digijet_android_app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
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
                saveLoginCredentials(email, password)
            } else {
                clearLoginCredentials()
            }

            login(email, password)
        }

        backButton.setOnClickListener {
            navigateToWelcomeActivity()
        }
    }

    private fun saveLoginCredentials(email: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.putBoolean("rememberMe", true)
        editor.apply()
    }

    private fun clearLoginCredentials() {
        val editor = sharedPreferences.edit()
        editor.remove("email")
        editor.remove("password")
        editor.remove("rememberMe")
        editor.apply()
    }

    private fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "loginWithEmail:success")
                    val editor = sharedPreferences.edit()
                    editor.putString("email", email)
                    editor.putString("password", password)
                    editor.putBoolean("rememberMe", true)
                    editor.apply()

                    GlobalScope.launch {
                        val nickname = getUserParamByEmail(email, "nickname")
                        val userId = getUserParamByEmail(email, "userId")

                        withContext(Dispatchers.Main) {
                            editor.putString("nickname", nickname)
                            editor.putString("userId", userId)
                            editor.apply()

                            navigateToMainPageActivity()
                        }
                    }
                } else {
                    Log.w(TAG, "loginWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private suspend fun getUserParamByEmail(email: String, param: String): String? =
        withContext(Dispatchers.IO) {
            val firestore = FirebaseFirestore.getInstance()
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                return@withContext null
            }

            val userDocument = querySnapshot.documents.first()

            return@withContext userDocument.getString(param)
        }

    private fun navigateToWelcomeActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToMainPageActivity() {
        val intent = Intent(this, MainPageActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
