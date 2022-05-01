package com.example.tingting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.tingting.databinding.FragmentChangePassWordBinding
import com.example.tingting.utils.onClick
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class changePassWordFragment : Fragment() {

    private lateinit var binding: FragmentChangePassWordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePassWordBinding.inflate(inflater)

        binding.ivBack.setOnClickListener {
            Navigation.findNavController(it).navigateUp()
        }
        binding.btnNext.onClick{
            val password = binding.edMobilePassword.text.toString()
            val rePassword = binding.edReMobilePassword.text.toString()

            if (password.isEmpty()) {
                binding.edMobilePassword.error = "Please enter new password"
                binding.edMobilePassword.requestFocus()
            }
            else if (password.length < 6) {
                binding.edMobilePassword.error = "11 should be at least 6 characters"
                binding.edMobilePassword.requestFocus()
            }
            else if (rePassword != password) {
                binding.edReMobilePassword.error = "Not the same as the password"
                binding.edReMobilePassword.requestFocus()
            } else {
                FirebaseAuth.getInstance().currentUser?.updatePassword(password)?.addOnSuccessListener {
                    Toast.makeText(context, "Change password successful", Toast.LENGTH_SHORT).show()
                }
            }

            Navigation.findNavController(binding.root).navigateUp()
        }
        return binding.root
    }

}