package com.example.tingting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.tingting.databinding.FragmentAppIntroductionBinding
import com.example.tingting.databinding.FragmentFirstBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AppIntroduction.newInstance] factory method to
 * create an instance of this fragment.
 */
class AppIntroduction : Fragment() {
    // TODO: Rename and change types of parameters

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

        }
        return binding.root
    }
}
