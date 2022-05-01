package com.example.tingting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.tingting.activity.LoginActivity
import com.example.tingting.activity.MainActivity
import com.example.tingting.databinding.FragmentSettingBinding
import com.example.tingting.utils.Entity.User
import com.example.tingting.utils.onClick
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSettingBinding.inflate(inflater)

        binding.tvFilter.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToFilterFragment()
            NavHostFragment.findNavController(this).navigate(action)
        }

        binding.tvSecurity.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToChangePassWordFragment()
            NavHostFragment.findNavController(this).navigate(action)
        }


        binding.ivBack.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        binding.ivProfile.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToViewProfileFragment()
            NavHostFragment.findNavController(this@SettingFragment).navigate(action)
        }

        // get current user

        val user = FirebaseAuth.getInstance().uid
        val mRef = FirebaseDatabase.getInstance().getReference("/Users/$user")

        // if user is not null, then user is logged in

        lifecycleScope.launch {
            mRef.addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        val user = p0.getValue(User::class.java)
                        if (user != null) {
                            binding.tvUsername.text = user.name
                            Glide.with(this@SettingFragment)
                                .load(user.avatar)
                                .into(binding.ivProfile)
                        }
                    }
                }
            })
        }

        if (user != null) {
            binding.tvLogout.visibility = View.VISIBLE
            binding.tvLogout.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }
        } else {
            binding.tvLogout.visibility = View.GONE
            binding.tvDeleteAccount.visibility = View.GONE
        }
        return binding.root
    }


}