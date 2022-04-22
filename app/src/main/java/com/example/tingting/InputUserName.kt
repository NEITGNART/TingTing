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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [inputUserName.newInstance] factory method to
 * create an instance of this fragment.
 */
class InputUserName : Fragment() {
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment inputUserName.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InputUserName().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

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