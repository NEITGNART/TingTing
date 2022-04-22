package com.example.tingting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.tingting.UserInfoFragmentArgs.Companion.fromBundle
import com.example.tingting.databinding.CongratulationFragmentBinding
import com.example.tingting.databinding.FragmentFirstBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Congratulation.newInstance] factory method to
 * create an instance of this fragment.
 */
class Congratulation : Fragment() {
    private lateinit var binding: CongratulationFragmentBinding

    val args: CongratulationArgs by navArgs()


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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CongratulationFragmentBinding.inflate(layoutInflater)

        val targetId = args.targetId
        val userId = FirebaseAuth.getInstance().uid!!
        val refTarget = FirebaseDatabase.getInstance().getReference("/Users/$targetId/avatar").get()

        val refCurrent = FirebaseDatabase.getInstance().getReference("/Users/$userId/avatar").get()



        refTarget.addOnSuccessListener {
            val imageUrl = it.getValue(String::class.java)
            Glide.with(binding.root.context).load(imageUrl).into(binding.ivProfile2)

        }
        refCurrent.addOnSuccessListener {
            val imageUrl = it.getValue(String::class.java)
            Glide.with(binding.root.context).load(imageUrl).into(binding.ivProfile1)

        }
        binding.tvSearch.setOnClickListener{
            Navigation.findNavController(binding.root).navigateUp()
        }





        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Congratulation.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Congratulation().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}