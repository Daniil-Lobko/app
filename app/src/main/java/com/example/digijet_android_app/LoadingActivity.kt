package com.example.digijet_android_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class LoadingActivity : AppCompatActivity() {

    private val fadeDuration = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        val logoImageView = findViewById<ImageView>(R.id.logoImageView)
        logoImageView.alpha = 0f
        logoImageView.animate()
            .setDuration(fadeDuration)
            .alpha(1f)
            .withEndAction {
                navigateToWelcomeActivity()
            }
    }

    private fun navigateToWelcomeActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}

