package com.example.digijet_android_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class LoadingActivity : AppCompatActivity() {

    private val loadingTimeMillis: Long = 3000 // Время загрузки в миллисекундах

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        val logoImageView: ImageView = findViewById(R.id.logoImageView)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        // Загрузка логотипа в ImageView
        logoImageView.setImageResource(R.drawable.logo_image)

        // Установка черного цвета для ProgressBar
        progressBar.indeterminateDrawable.setColorFilter(
            resources.getColor(android.R.color.black),
            android.graphics.PorterDuff.Mode.SRC_IN
        )

        // Здесь можно выполнить необходимые операции загрузки данных или выполнить асинхронные операции

        // Создаем задержку с использованием Handler
        Handler().postDelayed({
            // Переходим к WelcomeActivity
            val intent = Intent(this@LoadingActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish() // Закрываем LoadingActivity после перехода
        }, loadingTimeMillis)
    }
}
