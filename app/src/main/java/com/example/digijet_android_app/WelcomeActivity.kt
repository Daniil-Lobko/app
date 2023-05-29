package com.example.digijet_android_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val loginButton: Button = findViewById(R.id.loginButton)
        val registerButton: Button = findViewById(R.id.registerButton)
        val logoImageView: ImageView = findViewById(R.id.logoImageView)

        Glide.with(this)
            .load(R.drawable.logo_image)
            .apply(RequestOptions().transform(RoundedCorners(40)))
            .into(logoImageView)

        registerButton.setOnClickListener {
            // Здесь можно выполнить действия перед переходом к регистрации
            navigateToRegistration()
        }

        loginButton.setOnClickListener {
            // Здесь можно выполнить действия перед переходом к входу в систему
            navigateToLogin()
        }
    }

    private fun navigateToRegistration() {
        // Запуск страницы загрузки перед переходом к регистрации
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToLogin() {
        // Запуск страницы загрузки перед переходом к входу в систему
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}
