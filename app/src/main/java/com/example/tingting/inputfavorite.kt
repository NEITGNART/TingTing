package com.example.tingting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.tingting.databinding.FragmentInputUserPreferencesBinding
import com.example.tingting.databinding.FragmentInputfavoriteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [inputfavorite.newInstance] factory method to
 * create an instance of this fragment.
 */
class inputfavorite : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
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