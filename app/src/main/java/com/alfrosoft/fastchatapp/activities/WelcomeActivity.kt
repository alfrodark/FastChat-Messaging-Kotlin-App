package com.alfrosoft.fastchatapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alfrosoft.fastchatapp.databinding.ActivityWelcomeBinding
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerWelcomeBtn.setOnClickListener{

            val intent=Intent(this@WelcomeActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()

        }

        binding.loginWelcomeBtn.setOnClickListener{

            val intent=Intent(this@WelcomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }

    }

    override fun onStart() {
        super.onStart()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser!=null)
        {
            val intent=Intent(this@WelcomeActivity,
                MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}