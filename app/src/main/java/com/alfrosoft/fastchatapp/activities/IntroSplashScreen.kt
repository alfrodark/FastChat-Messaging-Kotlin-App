package com.alfrosoft.fastchatapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alfrosoft.fastchatapp.R
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.content.ContextCompat


class IntroSplashScreen : AppCompatActivity() {

    private lateinit var appLogoBack: ImageView
    private lateinit var appTextBack: ImageView

    override fun onStart() {
        super.onStart()
        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.splashScreen)

        window.navigationBarColor = ContextCompat.getColor(applicationContext, R.color.splashScreen)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_splash_screen)


        appLogoBack = findViewById(R.id.splash_screen_app_logo_back_iv)
        appTextBack = findViewById(R.id.splash_screen_app_logo_iv)

        appLogoBack.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_decrease_anim))
        appTextBack.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_increase_anim))

        Handler().postDelayed({

            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()

        }, 5000)

    }
}