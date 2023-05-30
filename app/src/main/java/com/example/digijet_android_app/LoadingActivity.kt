package com.example.digijet_android_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        val logoImageView: ImageView = findViewById(R.id.logoImageView)

        logoImageView.alpha = 0f
        logoImageView.animate().setDuration(2000).alpha(1f).withEndAction {
            val intent = Intent(this@LoadingActivity, WelcomeActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}
