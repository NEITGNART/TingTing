package com.example.tingting.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tingting.databinding.ActivityNotifycationBinding

class Notifycation : AppCompatActivity() {

    private lateinit var binding: ActivityNotifycationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotifycationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            finish()
        }

    }
}