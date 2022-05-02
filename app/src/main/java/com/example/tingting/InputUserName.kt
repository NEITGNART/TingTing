package com.example.tingting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.tingting.databinding.FragmentInputUserNameBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class InputUserName : Fragment() {




    private lateinit var binding: FragmentInputUserNameBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInputUserNameBinding.inflate(layoutInflater)
        binding.btnContinue.setOnClickListener{
            val userName = binding.etInUserName.text.toString()
            if (userName.isEmpty()) {
                binding.etInUserName.setBackgroundResource(R.drawable.error_border)
                Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT)
                    .show()
            }
            else {
                val user = FirebaseAuth.getInstance().currentUser
                val mDatabaseReference = FirebaseDatabase.getInstance().reference
                mDatabaseReference.child("Users").child(user?.uid.toString()).child("name").setValue(userName)
                val action = InputUserNameDirections.actionInputUserNameToInputUserBirthday()
                Navigation.findNavController(binding.root).navigate(action)
            }
        }

        binding.btnBack.setOnClickListener{
            val action = InputUserNameDirections.actionInputUserNameToAppIntroduction()
            Navigation.findNavController(binding.root).navigate(action)
        }

        return binding.root
    }
}