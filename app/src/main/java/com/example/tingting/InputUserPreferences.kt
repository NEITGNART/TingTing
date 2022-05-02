package com.example.tingting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.tingting.databinding.FragmentInputUserPreferencesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class InputUserPreferences : Fragment() {


    private lateinit var binding: FragmentInputUserPreferencesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInputUserPreferencesBinding.inflate(layoutInflater)
        val listOfCheckBox = ArrayList<CheckBox>()
        listOfCheckBox.add(binding.cbDisney)
        listOfCheckBox.add(binding.cbMusic)
        listOfCheckBox.add(binding.cbgame)
        listOfCheckBox.add(binding.cblife)
        listOfCheckBox.add(binding.cblovecat)
        listOfCheckBox.add(binding.cbwatchtv)
        listOfCheckBox.add(binding.rbNetflix)
        listOfCheckBox.add(binding.rbcungkn)



        binding.btnContinue.setOnClickListener{
            var message: String? = null
            listOfCheckBox.forEach { checkbox ->
                    if(checkbox.isChecked){
                        if (message == null)
                            message = checkbox.getText().toString()
                        else
                            message += "," + checkbox.getText().toString()
                    }
            }
            message = if (message == null) "You select nothing" else
            {
                "$message"
            }
            if(message != null){
                val userId = FirebaseAuth.getInstance().uid!!
                val list_favo: List<String> = message!!.split(",")
                for (i in list_favo.indices)
                {
                    val name = list_favo[i];
                    FirebaseDatabase.getInstance().getReference("/Setting/$userId/favorite/$name").setValue(name)
                }
            }

            val userId = FirebaseAuth.getInstance().uid!!

            FirebaseDatabase.getInstance().getReference("/Setting/$userId/distance/max").setValue(9)
            FirebaseDatabase.getInstance().getReference("/Setting/$userId/distance/min").setValue(0)

            FirebaseDatabase.getInstance().getReference("Setting/${FirebaseAuth.getInstance().uid}/age/min").setValue(0)
            FirebaseDatabase.getInstance().getReference("Setting/${FirebaseAuth.getInstance().uid}/age/max").setValue(7)



            Toast.makeText(binding.root.context, message.toString(), Toast.LENGTH_LONG).show()
            val action = InputUserPreferencesDirections.actionInputUserPreferencesToInputfavorite()
            Navigation.findNavController(binding.root).navigate(action)


        }
        //////////////////////////////////////////////////////////////////////////

        listOfCheckBox.forEach { checkbox ->
            checkbox.setOnClickListener{
                if(checkbox.isChecked){
                    checkbox.setBackgroundResource(R.drawable.gradient_border)
                    checkbox.setTextColor(resources.getColor(R.color.da_red1))
                }
                else {
                    checkbox.setBackgroundResource(R.drawable.gray_border)
                    checkbox.setTextColor(resources.getColor(R.color.black_80))
                }
            }
        }



        binding.btnBack.setOnClickListener{
            val action = InputUserPreferencesDirections.actionInputUserPreferencesToInputUserDisplay()
            Navigation.findNavController(binding.root).navigate(action)
        }


        return binding.root
    }




}
