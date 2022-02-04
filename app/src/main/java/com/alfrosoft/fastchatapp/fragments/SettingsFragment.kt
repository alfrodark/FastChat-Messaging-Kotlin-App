package com.alfrosoft.fastchatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alfrosoft.fastchatapp.R
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.storage.StorageReference
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.alfrosoft.fastchatapp.activities.SplashActivity
import com.alfrosoft.fastchatapp.databinding.FragmentSettingsBinding
import com.alfrosoft.fastchatapp.modelclasses.Users
import com.squareup.picasso.Picasso


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    lateinit var changeName:Button
    lateinit var socialMedia:Button
    lateinit var logOut:Button
    lateinit var ll1:LinearLayout

    var userReference:DatabaseReference?=null
    var firebaseUser: FirebaseUser?=null
    private var requestCode=438
    private var imageUri: Uri?=null
    private var storageRef:StorageReference?=null
    var coverChecker:String?=""
    var socialChecker:String=""

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent(), ActivityResultCallback {

            imageUri = it
            Toast.makeText(context,"uploading...",Toast.LENGTH_SHORT).show()
            uploadImageToDatabase()
        })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)


        firebaseUser= FirebaseAuth.getInstance().currentUser
        userReference=FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)
        storageRef=FirebaseStorage.getInstance().reference.child("User Images")

        logOut = binding.logOut
        changeName = binding.changeName
        socialMedia = binding.socialMedia
        ll1 = binding.ll1

        userReference!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                if(p0.exists())
                {
                    val user:Users?=p0.getValue(Users::class.java)

                    if (context!=null)
                    {
                        binding.usernameSettings.text=user!!.getUserName()
                        Picasso.get().load(user.getProfile()).into(binding.profileImageSettings)
                        Picasso.get().load(user.getCover()).into(binding.coverImageSettings)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {


            }
        })

        binding.profileImageSettings.setOnClickListener{
            pickImage()
        }

        binding.coverImageSettings.setOnClickListener{
            coverChecker="cover"
            pickImage()
        }

        binding.setFacebook.setOnClickListener{
            socialChecker="facebook"
            setSocialLinks()
        }

        binding.setInstagram.setOnClickListener{
            socialChecker="instagram"
            setSocialLinks()
        }

        binding.setWebsite.setOnClickListener{
            socialChecker="website"
            setSocialLinks()
        }

        logOut.setOnClickListener{
            logOutUser()

        }

        changeName.setOnClickListener{

            val builder:AlertDialog.Builder=
                AlertDialog.Builder(requireContext(),R.style.Theme_AppCompat_DayNight_Dialog_Alert )
            builder.setTitle("Write new Username:")
            val editText=EditText(context)
            editText.hint="e.g. John"
            builder.setView(editText)

            builder.setPositiveButton("Ok") { dialog, which ->
                val username = editText.text.toString()

                if (username == "") {
                    Toast.makeText(context, "Please write something...", Toast.LENGTH_SHORT).show()
                } else {
                    val userHashmap = HashMap<String, Any>()
                    userHashmap["username"] = username
                    userReference!!.updateChildren(userHashmap).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "updated successfully...", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
            builder.show()
        }

        socialMedia.setOnClickListener{

            if(ll1.visibility==View.GONE)
            {
                ll1.visibility=View.VISIBLE
            }
            else
            {
                ll1.visibility=View.GONE
            }
        }

        return binding.root
    }

    private fun logOutUser() {

        AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Attention")
            .setMessage("Are you sure you want to Log Out ?")
            .setIcon(R.drawable.ic_warning_24)
            .setPositiveButton("Yes") { _, _ ->

                FirebaseAuth.getInstance().signOut()

                val intent = Intent(activity as Context, SplashActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                requireActivity().finish()

            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create().show()
    }

    private fun setSocialLinks() {

        val builder:AlertDialog.Builder=
            AlertDialog.Builder(requireContext(),R.style.Theme_AppCompat_DayNight_Dialog_Alert )

        if (socialChecker=="website")
        {
            builder.setTitle("Write URL:")
        }
        else
        {
            builder.setTitle("Write Username:")
        }

        val editText=EditText(context)
        if (socialChecker=="website")
        {
            editText.hint="e.g. www.google.com"
        }
        else
        {
            editText.hint="e.g. john_1408"
        }
        builder.setView(editText)

        builder.setPositiveButton("Create") { dialog, which ->
            val str = editText.text.toString()

            if (str == "") {
                Toast.makeText(context, "Please write something...", Toast.LENGTH_SHORT).show()
            } else {
                saveSocialLink(str)
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun saveSocialLink(str:String) {
        val mapSocial=HashMap<String,Any>()


        when(socialChecker)
        {
            "facebook"->
            {
                mapSocial["facebook"] ="https://m.facebook.com/$str"
            }
            "instagram"->
            {
                mapSocial["instagram"] ="https://m.instagram.com/$str"
            }
            "website"->
            {
                mapSocial["website"] ="https://$str"
            }
        }
        userReference!!.updateChildren(mapSocial).addOnCompleteListener{task->
            if (task.isSuccessful)
            {
                Toast.makeText(context,"updated successfully...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImage() {

        activityResultLauncher.launch("image/*")

//        val intent=Intent()
//        intent.type="image/*"
//        intent.action=Intent.ACTION_GET_CONTENT
//        startActivityForResult(intent,RequestCode)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if(requestCode==RequestCode &&  resultCode==Activity.RESULT_OK && data!!.data!=null)
//        {
//            imageUri=data.data
//            Toast.makeText(context,"uploading...",Toast.LENGTH_SHORT).show()
//            uploadImageToDatabase()
//        }
//    }

    private fun uploadImageToDatabase() {
        val progressBar=ProgressDialog(context)
        progressBar.setMessage("image is uploading, please wait...")
        progressBar.show()

        if (imageUri != null)
        {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")

            val uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot,Task<Uri>>{task ->
                if (!task.isSuccessful)
                {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener{ task ->
                if (task.isSuccessful)
                {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if(coverChecker == "cover")
                    {
                        val mapCoverImg=HashMap<String,Any>()
                        mapCoverImg["cover"] = url
                        userReference!!.updateChildren(mapCoverImg)
                        coverChecker = ""
                    }
                    else
                    {
                        val mapProfileImg=HashMap<String,Any>()
                        mapProfileImg["profile"] = url
                        userReference!!.updateChildren(mapProfileImg)
                    }
                    progressBar.dismiss()
                }

            }
        }
    }


}