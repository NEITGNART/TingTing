package com.example.tingting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tingting.databinding.FragmentSecondBinding
import com.example.tingting.utils.Entity.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class SecondFragment : Fragment() {
    private lateinit var binding: FragmentSecondBinding
    private var searches = mutableListOf<User>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSecondBinding.inflate(layoutInflater)

        binding.rvList.layoutManager = GridLayoutManager(context, 2)

        loadMatchedFromFirebase()

        binding.rvList.adapter = UserAdapter(binding.root.context, searches)

        return binding.root
    }

    fun loadMatchedFromFirebase() {
        val userId = Firebase.auth.uid!!
        val ref = FirebaseDatabase.getInstance().getReference("/Matched/$userId")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                searches.clear()
                for (snapshot in dataSnapshot.children) {
                    val matchedId = snapshot.key

                    // get matched user from firebase

                    Log.i("matchedId", matchedId.toString())

                    val matchedRef =
                        FirebaseDatabase.getInstance().getReference("/Users/$matchedId")

                    matchedRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val matched = dataSnapshot.getValue(User::class.java)
                            searches.add(matched!!)
                            binding.rvList.adapter?.notifyDataSetChanged()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Failed to read value
                        }
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        })
    }
}


