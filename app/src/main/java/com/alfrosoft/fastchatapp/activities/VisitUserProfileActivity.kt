package com.alfrosoft.fastchatapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alfrosoft.fastchatapp.databinding.ActivityVisitUserProfileBinding
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.LinearLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.alfrosoft.fastchatapp.modelclasses.Users
import com.squareup.picasso.Picasso

class VisitUserProfileActivity : AppCompatActivity() {

    private  lateinit var binding: ActivityVisitUserProfileBinding

    private var userVisitId:String=""
    var user:Users?=null
    lateinit var ll1: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        userVisitId= intent.getStringExtra("visit_id").toString()
        ll1 = binding.ll1

        val ref=FirebaseDatabase.getInstance().reference.child("users").child(userVisitId)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                {
                    user=p0.getValue(Users::class.java)

                    binding.usernameDisplay.text=user!!.getUserName()
                    Picasso.get().load(user!!.getProfile()).into(binding.profileDisplay)
                    Picasso.get().load(user!!.getCover()).into(binding.coverDisplay)

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        binding.socialMedia.setOnClickListener{

            if(ll1.visibility== View.GONE)
            {
                ll1.visibility= View.VISIBLE
            }
            else
            {
                ll1.visibility= View.GONE
            }
        }

        binding.facebookDisplay.setOnClickListener{

            val uri=Uri.parse(user!!.getFacebook())

            val intent=Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)

        }
        binding.instagramDisplay.setOnClickListener{

            val uri=Uri.parse(user!!.getInstagram())

            val intent=Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)

        }
        binding.websiteDisplay.setOnClickListener{

            val uri=Uri.parse(user!!.getWebsite())

            val intent=Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)

        }

        binding.sendMsgBtn.setOnClickListener{

            val intent= Intent(this@VisitUserProfileActivity, MessageChatActivity::class.java)
            intent.putExtra("visit_id",user!!.getUID())
            startActivity(intent)

        }

    }

}