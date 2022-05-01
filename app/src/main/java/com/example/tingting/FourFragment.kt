package com.example.tingting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.tingting.databinding.FragmentFourBinding
import com.example.tingting.utils.Entity.Chat
import com.example.tingting.utils.Entity.User
import com.example.tingting.utils.setVerticalLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FourFragment : Fragment() {

    private lateinit var binding: FragmentFourBinding
    private val chats = mutableListOf<User>()
    private val currentUser = FirebaseAuth.getInstance().uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFourBinding.inflate(inflater)
        binding.tvMatches.setOnClickListener {
            val action = FourFragmentDirections.actionChatToFragmentMatches()
            Navigation.findNavController(binding.root).navigate(action)
        }

        // get list of chats from firebase realtime database

        // reference to the database
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Matched/$currentUser")

        val fromId = FirebaseAuth.getInstance().uid


        myRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: com.google.firebase.database.DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                chats.clear()
               for (i in p0.children) {
                    database.getReference("/Users/${i.key}").get()
                        .addOnSuccessListener{
                            val user = it.getValue(User::class.java)
                            if (user != null && user.id != currentUser) {
                                chats.add(user)


                                binding.rvChat.adapter = ChatAdapter(binding.root.context, chats)
                            }
                    }
                }
            }
        })

        binding.ivSearch.setOnClickListener {
//            val action = FourFragmentDirections.actionChatToFragmentSearch()
            val action = FourFragmentDirections.actionChatToSearchChatFragment()
            Navigation.findNavController(binding.root).navigate(action)
        }

        binding.rvChat.apply {
            setVerticalLayout()
        }

        return binding.root
    }

}

