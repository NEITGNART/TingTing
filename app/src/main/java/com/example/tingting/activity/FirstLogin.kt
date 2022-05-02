package com.example.tingting.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tingting.R
import com.example.tingting.utils.Entity.User
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FirstLogin : AppCompatActivity() {
    private lateinit var newUser: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        newUser = User()
        val authID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        FirebaseDatabase.getInstance().reference
            .child("Users").child(authID).child("id").setValue(authID)
        setContentView(R.layout.activity_first_login)


    }
}