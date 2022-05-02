package com.example.tingting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.tingting.databinding.FragmentInputUserDisplayBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class InputUserDisplay : Fragment() {

    private lateinit var binding: FragmentInputUserDisplayBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInputUserDisplayBinding.inflate(layoutInflater)
        binding.btnContinue.setOnClickListener{
            val userDisplay = binding.rgInUserDisplay.checkedRadioButtonId
            if (userDisplay == -1) {
                Toast.makeText(context, "Please select opponent's gender", Toast.LENGTH_SHORT)
                    .show()
            }
            else {
                val user = FirebaseAuth.getInstance().currentUser
                val mDatabaseReference = FirebaseDatabase.getInstance().reference
                var genderSelection = binding.root.findViewById<RadioButton>(userDisplay).text.toString()

                genderSelection = when (genderSelection) {
                    "NAM" -> "Male"
                    "NỮ" -> "Female"
                    else -> "All"
                }

                mDatabaseReference.child("Users").child(user?.uid.toString()).child("display")
                    .setValue(genderSelection)
                val action = InputUserDisplayDirections.actionInputUserDisplayToInputUserPreferences()
                Navigation.findNavController(binding.root).navigate(action)
            }
        }

        val count = binding.rgInUserDisplay.childCount
        val listOfRadioButtons = ArrayList<RadioButton>()
        for (i in 0 until count) {
            val o = binding.rgInUserDisplay.getChildAt(i)
            if (o is RadioButton) {
                listOfRadioButtons.add(o)
            }
        }

        binding.rgInUserDisplay.setOnCheckedChangeListener { radioGroup, i ->
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

        binding.btnBack.setOnClickListener{
            val action = InputUserDisplayDirections.actionInputUserDisplayToInputUserGender()
            Navigation.findNavController(binding.root).navigate(action)
        }

        // Inflate the layout for this fragment
        return binding.root
    }
}