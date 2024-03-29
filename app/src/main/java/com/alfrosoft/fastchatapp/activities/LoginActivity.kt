package com.alfrosoft.fastchatapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.alfrosoft.fastchatapp.R
import com.alfrosoft.fastchatapp.databinding.ActivityLoginBinding
import com.alfrosoft.fastchatapp.fragments.ForgotPassword


class LoginActivity : AppCompatActivity() {

    private  lateinit var binding: ActivityLoginBinding

    private  lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val toolbar: Toolbar =findViewById(R.id.toolbar_login)
        setSupportActionBar(binding.toolbarLogin)
        supportActionBar!!.title="Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent= Intent(this@LoginActivity,
                WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth=FirebaseAuth.getInstance()

        binding.loginBtn.setOnClickListener{
            loginUser()
        }

        binding.txtForgotPassword.setOnClickListener {
            userForgotPassword()
        }

        binding.btnGoToSignup.setOnClickListener {
            val intent=Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    private fun loginUser() {
        val email : String = binding.emailLogin.text.toString()
        val password : String = binding.passwordLogin.text.toString()

        if(email=="")
        {
            Toast.makeText(this@LoginActivity,"Please enter email address", Toast.LENGTH_SHORT).show()
        }
        else if(password=="")
        {
            Toast.makeText(this@LoginActivity,"Please enter Password", Toast.LENGTH_SHORT).show()
        }
        else
        {
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{task->
                if(task.isSuccessful)
                {
                    val intent=Intent(this@LoginActivity,
                        MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    Toast.makeText(this@LoginActivity,"Error Message: " + task.exception!!.message.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun userForgotPassword() {
        val forgotDialog = ForgotPassword()
        forgotDialog.show(supportFragmentManager, "Forgot Password Dialog")
    }

}