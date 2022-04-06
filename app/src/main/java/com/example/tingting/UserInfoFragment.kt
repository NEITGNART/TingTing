package com.example.tingting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.tingting.databinding.UserInfoFragmentBinding

class UserInfoFragment : Fragment() {
    lateinit var binding : UserInfoFragmentBinding
    lateinit var binding1 : UserInfoFragmentBinding


    companion object {
        fun newInstance() = UserInfoFragment()
    }

    private lateinit var viewModel: UserInfoViewModel
    lateinit var tv: EditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var list = mutableListOf<Int>()
        list.add(R.drawable.da_img16)
        list.add(R.drawable.da_img10)
        list.add(R.drawable.da_img12)

        //tv.setVisibility(View.INVISIBLE );
        binding =  UserInfoFragmentBinding.inflate(layoutInflater)
        var adapters = Adapters(binding.root.context)
        adapters.setContentList(list)
        var viewpager = binding.viewPagerMain
        viewpager.adapter = adapters
        viewModel = ViewModelProvider(this).get(UserInfoViewModel::class.java)

        binding.ivBack.setOnClickListener{
            Navigation.findNavController(binding.root).navigateUp()
        }
////        val args: TindercardstackDirections by navArgs()
//
//
//        val  action =   TindercardstackDirections.actionTindercardstackToUserInfoFragment()
        //tv.setText(action.toString())
        val amount: String = UserInfoFragmentArgs.fromBundle(requireArguments()).name
        binding.abcd.tvName.setText(amount)
        return binding.root
    }




    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

}