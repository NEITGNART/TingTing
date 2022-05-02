package com.example.tingting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.tingting.databinding.FragmentInputfavoriteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class inputfavorite : Fragment() {
    private lateinit var binding: FragmentInputfavoriteBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInputfavoriteBinding.inflate(layoutInflater)
        val mDatabaseReference = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser
        val userId = FirebaseAuth.getInstance().uid!!


        FirebaseDatabase.getInstance().getReference("/Users/$userId/work").get().addOnSuccessListener {
            if(it.value == null){
                binding.edtWork.setText("")
            }
            else {
                binding.edtWork.setText(it.value.toString())
            }
        }
        FirebaseDatabase.getInstance().getReference("/Users/$userId/about").get().addOnSuccessListener {
            if(it.value == null){
                binding.gtyourself.setText("")
            }
            else {
                binding.gtyourself.setText(it.value.toString())
            }
        }


        binding.btngtContinue.setOnClickListener {
            val userwork = binding.edtWork.text.toString()
            val useryourself = binding.gtyourself.text.toString()

            mDatabaseReference.child("Users").child(user?.uid.toString()).child("work")
                    .setValue(userwork)
            mDatabaseReference.child("Users").child(user?.uid.toString()).child("about")
                .setValue(useryourself)

            val action = inputfavoriteDirections.actionInputfavoriteToAddImage()
            Navigation.findNavController(binding.root).navigate(action)
        }
        binding.btngtBack.setOnClickListener {
            val action = inputfavoriteDirections.actionInputfavoriteToInputUserPreferences()
            Navigation.findNavController(binding.root).navigate(action)
        }
        return binding.root
    }


}