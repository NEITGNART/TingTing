package com.example.tingting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.tingting.databinding.FragmentInputUserBirthdayBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InputUserBirthday.newInstance] factory method to
 * create an instance of this fragment.
 */
class InputUserBirthday : Fragment() {
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
         * @return A new instance of fragment inputUserBirthday.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InputUserBirthday().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private lateinit var binding: FragmentInputUserBirthdayBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInputUserBirthdayBinding.inflate(layoutInflater)

        binding.btnContinue.setOnClickListener{
            val userBirthday = binding.etInUserBirthday.text.toString()
            if (userBirthday.isEmpty()) {
                binding.etInUserBirthday.setBackgroundResource(R.drawable.error_border)
                binding.etInUserBirthday.hint = "Hãy nhập ngày sinh của bạn"
            }
            else {
                val user = FirebaseAuth.getInstance().currentUser
                val mDatabaseReference = FirebaseDatabase.getInstance().reference
                mDatabaseReference.child("Users").child(user?.uid.toString()).child("birthDate")
                    .setValue(userBirthday)
                val action = InputUserBirthdayDirections.actionInputUserBirthdayToInputUserGender()
                Navigation.findNavController(binding.root).navigate(action)
            }
        }

        binding.btnBack.setOnClickListener{
            val action = InputUserBirthdayDirections.actionInputUserBirthdayToInputUserName()
            Navigation.findNavController(binding.root).navigate(action)
        }

        return binding.root
    }
}