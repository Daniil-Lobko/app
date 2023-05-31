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

                    val intent = Intent(this, MainPageActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Log.w(TAG, "loginWithEmail:failure", task.exception)
                    // Здесь можно обработать ошибку входа
                }
            }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
