package com.example.tingting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.tingting.databinding.FragmentFourBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FourFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FourFragment : Fragment() {

    private lateinit var binding: FragmentFourBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragmentFourBinding.inflate(inflater)
        binding.tvMatches.setOnClickListener {
            val action = FourFragmentDirections.actionChatToFragmentMatches()
            Navigation.findNavController(binding.root).navigate(action)
        }

        binding.rvChat.setOnClickListener {

        }

        return binding.root
    }

}