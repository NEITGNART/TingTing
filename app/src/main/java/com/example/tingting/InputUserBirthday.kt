package com.example.tingting

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.tingting.databinding.FragmentInputUserBirthdayBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*


class InputUserBirthday : Fragment() {


    private lateinit var binding: FragmentInputUserBirthdayBinding

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInputUserBirthdayBinding.inflate(layoutInflater)


        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)



        binding.button2.setOnClickListener {
            val dpd = DatePickerDialog(
                binding.root.context,
                DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                    var month = mMonth
                    month.toInt()
                    month = month + 1;
                    if(month <10) {
                        binding.etInUserBirthday.setText("" + mDay + "/" + "0" + month.toString() + "/" + mYear)
                    }
                    else{
                        binding.etInUserBirthday.setText("" + mDay + "/"  + month.toString() + "/" + mYear)
                    }

                },
                year,
                month,
                day
            )

            dpd.show()
        }

        binding.btnContinue.setOnClickListener {
            val userBirthday = binding.etInUserBirthday.text.toString()
            val yearOfBirth = binding.etInUserBirthday.text!!.split("/")[2].toInt()
            val age = java.util.Calendar.getInstance()
                .get(java.util.Calendar.YEAR) - yearOfBirth
            val month = java.util.Calendar.getInstance()
                .get(java.util.Calendar.MONTH) - binding.etInUserBirthday.text!!.split("/")[1].toInt()
            val day = java.util.Calendar.getInstance()
                .get(java.util.Calendar.DAY_OF_WEEK_IN_MONTH) - binding.etInUserBirthday.text!!.split("/")[0].toInt()
            if (userBirthday.isEmpty()) {
                binding.etInUserBirthday.setBackgroundResource(R.drawable.error_border)
                Toast.makeText(context, "Please enter your birthday", Toast.LENGTH_SHORT)
                    .show()
            }
            else if(age <= 0) {
                if(month <0 || (month ==0 && day<= 0)){
                    binding.etInUserBirthday.setBackgroundResource(R.drawable.error_border)
                    Toast.makeText(context, "Ngày sinh bạn không phù hợp", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            else if(age in 1..17) {
                binding.etInUserBirthday.setBackgroundResource(R.drawable.error_border)
                Toast.makeText(context, "Bạn chưa đủ 18 tuổi nên không thể tham gia được", Toast.LENGTH_SHORT)
                    .show()
            }

            else
            {
                val user = FirebaseAuth.getInstance().currentUser
                val mDatabaseReference = FirebaseDatabase.getInstance().reference
                mDatabaseReference.child("Users").child(user?.uid.toString()).child("birthDate")
                    .setValue(userBirthday)
                val action = InputUserBirthdayDirections.actionInputUserBirthdayToInputUserGender()
                Navigation.findNavController(binding.root).navigate(action)
            }
        }

        binding.btnBack.setOnClickListener {
            val action = InputUserBirthdayDirections.actionInputUserBirthdayToInputUserName()
            Navigation.findNavController(binding.root).navigate(action)
        }

        return binding.root
    }


}