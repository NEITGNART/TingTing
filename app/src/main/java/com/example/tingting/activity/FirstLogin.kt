package com.example.tingting.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tingting.R
import com.example.tingting.utils.Entity.User

class FirstLogin : AppCompatActivity() {
    private lateinit var newUser: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        newUser = User()
        setContentView(R.layout.activity_first_login)
    }
}