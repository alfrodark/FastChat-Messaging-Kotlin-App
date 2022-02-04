package com.alfrosoft.fastchatapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alfrosoft.fastchatapp.adapterclasses.UserAdapter
import com.alfrosoft.fastchatapp.databinding.FragmentChatsBinding
import com.alfrosoft.fastchatapp.modelclasses.Users
import com.alfrosoft.fastchatapp.notifications.Token
import com.alfrosoft.fastchatapp.modelclasses.ChatList
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var userAdapter: UserAdapter? = null
    private var mUsers:List<Users>? = null
    private var usersChatList:List<ChatList>? = null
    lateinit var recycler_view_chatlist: RecyclerView
    private var firebaseUser: FirebaseUser? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentChatsBinding.inflate(inflater, container, false)

        recycler_view_chatlist = binding.recyclerViewChatlist
        recycler_view_chatlist.setHasFixedSize(true)
        recycler_view_chatlist.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser


        usersChatList=ArrayList()


        val ref = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                (usersChatList as ArrayList).clear()

                for(dataSnapshot in p0.children)
                {
                    val chatlist=dataSnapshot.getValue(ChatList::class.java)

                    (usersChatList as ArrayList).add(chatlist!!)
                }
                retrieveChatList()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        updateToken(FirebaseMessaging.getInstance().token)

        return binding.root

    }

    private fun updateToken(token: Task<String>) {
        val ref=FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }

    private fun retrieveChatList()
    {
        mUsers=ArrayList()

        val ref=FirebaseDatabase.getInstance().reference.child("users")
        ref!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                (mUsers as ArrayList).clear()

                for (dataSnapshot in p0.children)
                {
                    val user=dataSnapshot.getValue(Users::class.java)

                    for (eachChatList in usersChatList!!)
                    {
                        if (user!!.getUID().equals(eachChatList.getID()))
                        {
                            (mUsers as ArrayList).add(user!!)
                        }
                    }
                }
                userAdapter= UserAdapter(context!!,(mUsers as ArrayList<Users>),true)
                recycler_view_chatlist.adapter=userAdapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}