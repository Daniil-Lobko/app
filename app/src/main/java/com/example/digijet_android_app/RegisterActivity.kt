package com.example.digijet_android_app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
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
            navigateToWelcomeActivity()
        }
    }

    private fun register(email: String, password: String, nickname: String, phoneNumber: String) {
        if (password.length < 6) {
            showErrorMessage("Password must be at least 6 characters long")
            return
        }

        if (phoneNumber.length < 10) {
            showErrorMessage("Invalid phone number")
            return
        }

        if (nickname.isEmpty()) {
            showErrorMessage("Nickname cannot be empty")
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    handleRegistrationSuccess(email, password, task.result?.user?.uid, nickname, phoneNumber)
                } else {
                    handleRegistrationFailure(task.exception)
                }
            }
    }

    private fun handleRegistrationSuccess(email: String, password: String, userId: String?, nickname: String, phoneNumber: String) {
        Log.d(TAG, "registerWithEmail:success")

        saveUserDataToFirestore(email, password, userId, nickname, phoneNumber)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.putString("userId", userId)
        editor.putString("nickname", nickname)
        editor.putString("phoneNumber", phoneNumber)
        editor.putBoolean("rememberMe", true)
        editor.apply()

        navigateToMainPageActivity()
    }

    private fun handleRegistrationFailure(exception: Exception?) {
        Log.w(TAG, "registerWithEmail:failure", exception)

        val errorMessage = when (exception) {
            is FirebaseAuthUserCollisionException -> "The email address is already in use"
            is FirebaseAuthInvalidCredentialsException -> "Invalid email format"
            else -> "Registration failed"
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
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

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "RegisterActivity"
    }
}
