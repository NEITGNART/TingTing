package com.example.tingting

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.tingting.databinding.UserInfoFragmentBinding

class UserInfoFragment : Fragment() {
    lateinit var binding : UserInfoFragmentBinding

    companion object {
        fun newInstance() = UserInfoFragment()
    }

    private lateinit var viewModel: UserInfoViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var list = mutableListOf<Int>()
        list.add(R.drawable.image1)
        list.add(R.drawable.image2)
        list.add(R.drawable.image3)

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
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

}