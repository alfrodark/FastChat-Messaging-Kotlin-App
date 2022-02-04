package com.alfrosoft.fastchatapp.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.alfrosoft.fastchatapp.R
import com.alfrosoft.fastchatapp.adapterclasses.ViewPagerAdapter
import com.alfrosoft.fastchatapp.databinding.ActivityMainBinding
import com.alfrosoft.fastchatapp.fragments.ChatsFragment
import com.alfrosoft.fastchatapp.fragments.SearchFragment
import com.alfrosoft.fastchatapp.fragments.SettingsFragment
import com.alfrosoft.fastchatapp.modelclasses.Chat
import com.alfrosoft.fastchatapp.modelclasses.Users
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding


    private val mOnNavigationItemSelectedListener = NavigationBarView.OnItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_chats -> {

                binding.viewPager.currentItem = 0
                return@OnItemSelectedListener true
            }
            R.id.navigation_search -> {

                binding.viewPager.currentItem = 1
                return@OnItemSelectedListener true
            }
            R.id.navigation_settings -> {

                binding.viewPager.currentItem = 2
                return@OnItemSelectedListener true
            }
        }
        false
    }

    var refUsers: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    lateinit var progressLayout: RelativeLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMain)

        val navView: BottomNavigationView = binding.navigation
        val tabLayout: TabLayout = binding.tabLayout
        val viewPager: ViewPager2 = binding.viewPager
        val viewPagerAdapter= ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter

        navView.setOnItemSelectedListener(mOnNavigationItemSelectedListener)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> navView.menu.findItem(R.id.navigation_chats).isChecked = true
                    1 -> navView.menu.findItem(R.id.navigation_search).isChecked = true
                    2 -> navView.menu.findItem(R.id.navigation_settings).isChecked = true
                }
            }
        })

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
            R.id.navigation_chats, R.id.navigation_search, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        progressLayout=findViewById(R.id.progressLayout)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)

        val toolbar: Toolbar = binding.toolbarMain
        setSupportActionBar(binding.toolbarMain)
        supportActionBar!!.title = ""


        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                var countUnreadMessages=0

                for (dataSnapshot in p0.children)
                {
                    val chat=dataSnapshot.getValue(Chat::class.java)

                    if (chat!!.getReceiver().equals(firebaseUser!!.uid) && !chat.isIsSeen())
                    {
                        countUnreadMessages += 1
                    }
                }
                if (countUnreadMessages==0)
                {
                    viewPagerAdapter.addFragment(ChatsFragment(),"Chats")
                }
                else
                {
                    viewPagerAdapter.addFragment(ChatsFragment(),"Chats ($countUnreadMessages)")
                }
                viewPagerAdapter.addFragment(SearchFragment(),"Search")
                viewPagerAdapter.addFragment(SettingsFragment(),"Settings")
                viewPager.adapter = viewPagerAdapter
                TabLayoutMediator(binding.tabLayout, viewPager) { tab, position ->
                    when (position) {
                        0 -> tab.text = "Chats"
                        1 -> tab.text = "Search"
                        2 -> tab.text = "Settings"
                    }
                }.attach()
                binding.tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_chat_24)
                binding.tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_person_search_24)
                binding.tabLayout.getTabAt(2)?.setIcon(R.drawable.ic_settings_24)
//                binding.tabLayout.setupWithViewPager(viewPager)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        //display username and profile picture
        refUsers!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                {
                    val user =p0.getValue(Users::class.java)

                    binding.userName.text = user!!.getUserName()
                    Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(binding.profileImage)
                    progressLayout.visibility = View.GONE
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_logout ->
            {
                logOutUser()

                return true
            }
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    internal  class ViewPageAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager)
    {
        private var fragments:ArrayList<Fragment> = ArrayList<Fragment>()
        private var titles:ArrayList<String> = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: Fragment,title:String)
        {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }
    }

    private fun logOutUser() {

        AlertDialog.Builder(this@MainActivity, R.style.AlertDialogTheme)
            .setTitle("Attention")
            .setMessage("Are you sure you want to Log Out ?")
            .setIcon(R.drawable.ic_warning_24)
            .setPositiveButton("Yes") { _, _ ->

                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this@MainActivity, SplashActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create().show()
    }

    private fun updateStatus(status:String)
    {
        val ref=FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)

        val hashmap= HashMap<String,Any>()
        hashmap["status"] = status
        ref.updateChildren(hashmap)
    }

    override fun onResume() {
        super.onResume()
        updateStatus("online")
    }

    override fun onPause() {
        super.onPause()
        updateStatus("offline")
    }

}