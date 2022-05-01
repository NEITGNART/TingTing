package com.example.tingting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.tingting.databinding.CongratulationFragmentBinding
import com.example.tingting.utils.Entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class Congratulation : Fragment() {
    private lateinit var binding: CongratulationFragmentBinding

    private val args: CongratulationArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CongratulationFragmentBinding.inflate(layoutInflater)

        val targetId = args.targetId
        val userId = FirebaseAuth.getInstance().uid!!
        FirebaseDatabase.getInstance().getReference("/Users/$targetId/avatar").get()
            .addOnSuccessListener {
                Glide.with(binding.root.context).load(it.value).into(binding.ivProfile2)
            }
        FirebaseDatabase.getInstance().getReference("/Users/$userId/avatar").get()
            .addOnSuccessListener {
                Glide.with(binding.root.context).load(it.value).into(binding.ivProfile1)
            }
        binding.tvSearch.setOnClickListener {
            Navigation.findNavController(it).navigateUp()
        }

        binding.btnSendMessage.setOnClickListener {

            FirebaseDatabase.getInstance().getReference("/Users/${targetId}").get()
                .addOnSuccessListener {
                    val intent = Intent(binding.root.context, ChatActivity::class.java)
                    val user = it.getValue(User::class.java)
                    intent.putExtra("toUser", user)
                    ContextCompat.startActivity(binding.root.context, intent, null)
                }

        }

        return binding.root
    }
}