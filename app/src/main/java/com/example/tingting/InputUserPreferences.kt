package com.example.tingting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.tingting.databinding.FragmentInputUserPreferencesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [inputUserPreferences.newInstance] factory method to
 * create an instance of this fragment.
 */
class InputUserPreferences : Fragment() {
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
         * @return A new instance of fragment inputUserPreferences.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InputUserPreferences().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

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
            FirebaseDatabase.getInstance().getReference("/Setting/$userId/distance/max").setValue(20)
            FirebaseDatabase.getInstance().getReference("/Setting/$userId/distance/min").setValue(0)

            FirebaseDatabase.getInstance().getReference("/Setting/$userId/age/min").setValue(18)
            FirebaseDatabase.getInstance().getReference("/Setting/$userId/age/max").setValue(30)




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


        ///////////////////////////////////////////////////////////////////////
//        binding.btnContinue.setOnClickListener{
//            val action = InputUserPreferencesDirections.actionInputUserPreferencesToAddImage()
//            Navigation.findNavController(binding.root).navigate(action)
//        }

        binding.btnBack.setOnClickListener{
            val action = InputUserPreferencesDirections.actionInputUserPreferencesToInputUserDisplay()
            Navigation.findNavController(binding.root).navigate(action)
        }


        return binding.root
    }




}
