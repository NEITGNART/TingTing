package com.example.tingting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.tingting.activity.LoginActivity
import com.example.tingting.databinding.FragmentAppIntroductionBinding

class AppIntroduction : Fragment() {

    private lateinit var binding: FragmentAppIntroductionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAppIntroductionBinding.inflate(layoutInflater)
        binding.btnAccess.setOnClickListener{
            val action = AppIntroductionDirections.actionAppIntroductionToInputUserName()
            Navigation.findNavController(binding.root).navigate(action)
        }

        binding.btnClose.setOnClickListener{
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }


        return binding.root
    }
}
