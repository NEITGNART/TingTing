package com.example.tingting.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.tingting.ChatActivity
import com.example.tingting.databinding.ActivityCongratulationBinding
import com.example.tingting.utils.Entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CongratulationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCongratulationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCongratulationBinding.inflate(layoutInflater)

        val targetId = intent.getStringExtra("targetId")

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
            onBackPressed()
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


        setContentView(binding.root)
    }
}