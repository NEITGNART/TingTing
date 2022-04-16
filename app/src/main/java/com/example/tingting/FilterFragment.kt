package com.example.tingting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.Navigation
import com.example.tingting.databinding.DaBottomSheetActivityBinding
import com.example.tingting.databinding.FragmentFilterBinding
import com.example.tingting.utils.onClick
import com.google.android.material.bottomsheet.BottomSheetDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FilterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FilterFragment : Fragment() {

    private lateinit var binding: FragmentFilterBinding
    private var dialog: BottomSheetDialog? = null
    private val distanceArray =
        arrayOf("1 km", "2 km", "3 km", "4 km", "5 km", "6 km", "7 km", "8 km", "9 km", "10 km")
    private val ageArray2 = arrayOf("18", "19", "20", "21", "22", "23", "24", "25")



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFilterBinding.inflate(inflater)
        binding.ivBack.setOnClickListener {
            Navigation.findNavController(it).navigateUp()
        }


        binding.rangebarDistance.tickTopLabels = distanceArray
        binding.rangebarAge.tickTopLabels = ageArray2

        binding.tvLocation.onClick {
        }
        binding.tvGender.onClick {
        }

        return binding.root

    }


}