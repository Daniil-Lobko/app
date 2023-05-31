package com.example.digijet_android_app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseAuth = FirebaseAuth.getInstance()

        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val nicknameEditText: EditText = findViewById(R.id.nicknameEditText)
        val phoneNumberEditText: EditText = findViewById(R.id.phoneNumberEditText)
        val registerButton: Button = findViewById(R.id.registerButton)
        val backButton: Button = findViewById(R.id.backButton)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val nickname = nicknameEditText.text.toString()
            val phoneNumber = phoneNumberEditText.text.toString()

            register(email, password, nickname, phoneNumber)
        }

        backButton.setOnClickListener {
            // Переадресация на экран WelcomeActivity
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun register(email: String, password: String, nickname: String, phoneNumber: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "registerWithEmail:success")
                    val firebaseUser = task.result?.user

                    // Сохраняем данные пользователя в Cloud Firestore
                    saveUserDataToFirestore(email, password, firebaseUser?.uid, nickname, phoneNumber)

                    // Сохраняем данные сессии юзера
                    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                    val editor = sharedPreferences.edit()
                    editor.putString("email", email)
                    editor.putString("password", password)
                    editor.putString("userId", firebaseUser?.uid)
                    editor.putString("nickname", nickname)
                    editor.putString("phoneNumber", phoneNumber)
                    editor.putBoolean("rememberMe", true)
                    editor.apply()

                    // Здесь можно перейти на другую активити после успешной регистрации
                    val intent = Intent(this, MainPageActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Log.w(TAG, "registerWithEmail:failure", task.exception)
                    // Здесь можно обработать ошибку регистрации
                }
            }
    }

    private fun saveUserDataToFirestore(email: String, password: String, userId: String?, nickname: String, phoneNumber: String) {
        val firestore = FirebaseFirestore.getInstance()
        val userCollection = firestore.collection("users")
        val userDocument = userCollection.document(userId ?: "")
        val userData = hashMapOf(
            "email" to email,
            "password" to password,
            "userId" to userId,
            "nickname" to nickname,
            "phoneNumber" to phoneNumber
        )

        userDocument.set(userData)
            .addOnSuccessListener {
                Log.d(TAG, "User data saved to Firestore")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error saving user data to Firestore", e)
            }
    }


    companion object {
        private const val TAG = "RegisterActivity"
    }
}
