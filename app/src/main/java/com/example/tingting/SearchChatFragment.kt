package com.example.tingting

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tingting.databinding.FragmentSearchChatBinding
import com.example.tingting.utils.Entity.User
import com.example.tingting.utils.onClick
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class SearchChatFragment : Fragment() {

    private val args: SearchChatFragmentArgs by navArgs()
    private lateinit var binding: FragmentSearchChatBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSearchChatBinding.inflate(inflater)

        binding.tvCancel.onClick {
            Navigation.findNavController(binding.root).navigateUp()
        }

        binding.rvPeople.apply {
            layoutManager = LinearLayoutManager(context)
        }

        binding.edtSearch.setOnClickListener {

        }


        // reference to the database
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Matched/${Firebase.auth.currentUser!!.uid}")
        getMatched(myRef, database, binding, false)

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // do nothing
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.toString().isNotEmpty()) {
                    getMatched(myRef, database, binding, true)

                } else {
                    getMatched(myRef, database, binding, false)
                }
            }

        })

        return binding.root
    }

    fun getMatched(
        myRef: DatabaseReference,
        database: FirebaseDatabase,
        binding: FragmentSearchChatBinding,
        flag: Boolean = false
    ) {
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val chats = mutableListOf<User>()
                for (i in p0.children) {
                    database.getReference("/Users/${i.key}").get()
                        .addOnSuccessListener {
                            val user = it.getValue(User::class.java)
                            if (!flag) {
                                if (user != null && user.id != Firebase.auth.currentUser!!.uid) {
                                    chats.add(user)
                                    binding.rvPeople.adapter =
                                        ChatAdapter(binding.root.context, chats, args.matched)
                                }
                            } else {
                                if (user != null && user.id != Firebase.auth.currentUser!!.uid && user.name!!.contains(
                                        binding.edtSearch.text.toString()
                                    )
                                ) {
                                    chats.add(user)
                                    binding.rvPeople.adapter =
                                        ChatAdapter(binding.root.context, chats, args.matched)
                                }
                            }

                        }
                }
            }
        })
    }

}