package com.example.tingting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.tingting.databinding.FragmentFirstBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FirstFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FirstFragment : Fragment() {

    private lateinit var binding: FragmentFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFirstBinding.inflate(layoutInflater)

        // Code here !!!
        binding.btprofile.setOnClickListener{
            val action = FirstFragmentDirections.actionFirstFragmentToUserInfoFragment()
//            findNavController().navigate(action)
            Navigation.findNavController(binding.root).navigate(action)
//            Navigation.findNavController(binding.root).navigateUp()

        }
        binding.viewcardstack.setOnClickListener{
            val action = FirstFragmentDirections.actionHomepageToTindercardstack()
//            findNavController().navigate(action)
            Navigation.findNavController(binding.root).navigate(action)
//            Navigation.findNavController(binding.root).navigateUp()

        }

        return binding.root
    }

}