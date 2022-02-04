package com.alfrosoft.fastchatapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import android.widget.Toast
import com.alfrosoft.fastchatapp.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {

    private  lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Toast.makeText(this@SplashActivity,"Logging Out...", Toast.LENGTH_LONG).show()

        Handler().postDelayed({
            val intent= Intent(this@SplashActivity, WelcomeActivity::class.java)
            startActivity(intent)
        },3000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}