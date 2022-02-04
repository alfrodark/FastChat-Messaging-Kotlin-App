package com.alfrosoft.fastchatapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alfrosoft.fastchatapp.R
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.alfrosoft.fastchatapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.HashMap


class RegisterActivity : AppCompatActivity() {

    private  lateinit var binding: ActivityRegisterBinding

    private  lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserID: String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val toolbar: Toolbar =findViewById(R.id.toolbar_register)
        setSupportActionBar(binding.toolbarRegister)
        supportActionBar!!.title="Create New Account"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent= Intent(this@RegisterActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth=FirebaseAuth.getInstance()

        binding.registerBtn.setOnClickListener{
            registerUser()
        }

        binding.btnGoToLoginHere.setOnClickListener {
            val intent= Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun registerUser() {

        val username : String = binding.usernameRegister.text.toString()
        val email : String = binding.emailRegister.text.toString()
        val password : String = binding.passwordRegister.text.toString()

        if(username=="")
        {
            Toast.makeText(this@RegisterActivity,"Please enter username",Toast.LENGTH_SHORT).show()
        }
        else if(email=="")
        {
            Toast.makeText(this@RegisterActivity,"Please enter email address",Toast.LENGTH_SHORT).show()
        }
        else if(password=="")
        {
            Toast.makeText(this@RegisterActivity,"Please enter Password",Toast.LENGTH_SHORT).show()
        }
        else
        {
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{task->
                if(task.isSuccessful)
                {
                    firebaseUserID=mAuth.currentUser!!.uid
                    refUsers=FirebaseDatabase.getInstance().reference.child("users").child(firebaseUserID)

                    val userHashMap=HashMap<String, Any>()
                    userHashMap["uid"]=firebaseUserID
                    userHashMap["username"]=username
                    userHashMap["profile"]="https://firebasestorage.googleapis.com/v0/b/chatty-4c870.appspot.com/o/profile.png?alt=media&token=78eab942-e65f-4e3d-9cd6-bc0b57d2f043"
                    userHashMap["cover"]="https://firebasestorage.googleapis.com/v0/b/chatty-4c870.appspot.com/o/cover.jpg?alt=media&token=cdd71991-3ce6-4cea-aaa0-a41ea910932d"
                    userHashMap["status"]="offline"
                    userHashMap["search"]= username.lowercase(Locale.getDefault())
                    userHashMap["facebook"]="https://m.facebook.com"
                    userHashMap["instagram"]="https://m.instagram.com"
                    userHashMap["website"]="https://m.google.com"

                    refUsers.updateChildren(userHashMap).addOnCompleteListener{task->
                        if(task.isSuccessful)
                        {
                            val intent=Intent(this@RegisterActivity,
                                MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                    }

                }
                else
                {
                    Toast.makeText(this@RegisterActivity,"Error Message: " + task.exception!!.message.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}