package com.example.tingting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tingting.databinding.FragmentSearchChatBinding
import com.example.tingting.utils.Entity.User
import com.example.tingting.utils.onClick
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase


class SearchChatFragment : Fragment() {


    private lateinit var binding: FragmentSearchChatBinding
    private val chats = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSearchChatBinding.inflate(inflater)

        binding.tvCancel.onClick {
            Navigation.findNavController(binding.root).navigateUp()
        }

        binding.rvPeople.apply {
            adapter = SearchChatAdapter(binding.root.context, chats)
            layoutManager = LinearLayoutManager(context)
        }

        // reference to the database
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Matched/${Firebase.auth.currentUser!!.uid}")

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
                            if (user != null && user.id != Firebase.auth.currentUser!!.uid) {
                                chats.add(user)
                                binding.rvPeople.adapter = ChatAdapter(binding.root.context, chats)
                            }
                        }
                }
            }
        })

        return binding.root
    }

}