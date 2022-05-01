package com.example.tingting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.tingting.databinding.FragmentChangePassWordBinding

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

        return binding.root
    }

}