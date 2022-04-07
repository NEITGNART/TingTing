package com.example.tingting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.tingting.databinding.FragmentFirstBinding
import com.example.tingting.databinding.FragmentSecondBinding


class SecondFragment : Fragment() {
    private lateinit var binding: FragmentSecondBinding
//    private lateinit var viewModel: SecondFragmentViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSecondBinding.inflate(layoutInflater)
        return binding.root
    }

}