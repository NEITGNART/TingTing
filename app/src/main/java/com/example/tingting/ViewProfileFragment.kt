package com.example.tingting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.tingting.databinding.FragmentViewProfileBinding
import com.example.tingting.utils.Entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewProfileFragment : Fragment() {

    private lateinit var binding: FragmentViewProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentViewProfileBinding.inflate(inflater)
        val userId = FirebaseAuth.getInstance().uid!!

        FirebaseDatabase.getInstance().getReference("/Users/$userId/about").get().addOnSuccessListener {
            if(it.value == null)
                binding.tvAbout.setText("")
            else
                binding.tvAbout.setText(it.value.toString())
        }
        FirebaseDatabase.getInstance().getReference("/Users/$userId/work").get().addOnSuccessListener {
            if(it.value == null)
                binding.tvProfession.setText("")
            else
                binding.tvProfession.setText(it.value.toString())
        }


        binding.ivEdit.setOnClickListener {
            val action = ViewProfileFragmentDirections.actionViewProfileFragmentToProfileFragment()
            NavHostFragment.findNavController(this).navigate(action)
        }
        // get id of current user

        FirebaseDatabase.getInstance().getReference("/Users/${FirebaseAuth.getInstance().currentUser!!.uid}")
            .addValueEventListener(object :
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: com.google.firebase.database.DataSnapshot) {
                    val user = p0.getValue(User::class.java)
                    binding.tvName.text = user?.name
                    Glide.with(this@ViewProfileFragment)
                        .load(user?.avatar)
                        .into(binding.profileImage)
                }

            })

        binding.ivBack.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }

        return binding.root
    }

}