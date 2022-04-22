package com.example.tingting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.tingting.databinding.FragmentInputUserGenderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InputUserGender.newInstance] factory method to
 * create an instance of this fragment.
 */
class InputUserGender : Fragment() {
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
         * @return A new instance of fragment inputUserGender.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InputUserGender().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private lateinit var binding: FragmentInputUserGenderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInputUserGenderBinding.inflate(layoutInflater)

        /////////////////////////////////////////////////////////////////////////
        binding.btnContinue.setOnClickListener{
            val userGender = binding.rgInUserGender.checkedRadioButtonId
            if (userGender == -1) {
                Toast.makeText(context, "Please select your gender", Toast.LENGTH_SHORT)
                    .show()
            }
            else {
                val user = FirebaseAuth.getInstance().currentUser
                val mDatabaseReference = FirebaseDatabase.getInstance().reference
                var genderSelection = binding.root.findViewById<RadioButton>(userGender).text.toString()

                genderSelection = when (genderSelection) {
                    "NAM" -> "Male"
                    "NỮ" -> "Female"
                    else -> "Other"
                }

                mDatabaseReference.child("Users").child(user?.uid.toString()).child("gender")
                    .setValue(genderSelection)
                val action = InputUserGenderDirections.actionInputUserGenderToInputUserDisplay()
                Navigation.findNavController(binding.root).navigate(action)
            }
        }

        ////////////////////////////////////////////////////////////////////////
        val count = binding.rgInUserGender.childCount
        val listOfRadioButtons = ArrayList<RadioButton>()
        for (i in 0 until count) {
            val o = binding.rgInUserGender.getChildAt(i)
            if (o is RadioButton) {
                listOfRadioButtons.add(o)
            }
        }


        ////////////////////////////////////////////////////////////////////////
        binding.rgInUserGender.setOnCheckedChangeListener { radioGroup, i ->
            listOfRadioButtons.forEach {radioButton ->
                if (radioButton.isChecked){
                    radioButton.setBackgroundResource(R.drawable.gradient_border)
                    radioButton.setTextColor(resources.getColor(R.color.da_red1))
                }
                else {
                    radioButton.setBackgroundResource(R.drawable.gray_border)
                    radioButton.setTextColor(resources.getColor(R.color.black_80))
                }
            }
        }

        ////////////////////////////////////////////////////////////////////////
        binding.btnBack.setOnClickListener{
            val action = InputUserGenderDirections.actionInputUserGenderToInputUserBirthday()
            Navigation.findNavController(binding.root).navigate(action)
        }

        // Inflate the layout for this fragment
        return binding.root
    }
}