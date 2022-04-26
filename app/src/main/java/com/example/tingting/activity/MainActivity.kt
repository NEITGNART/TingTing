package com.example.tingting.activity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.tingting.R
import com.example.tingting.SettingActivity
import com.example.tingting.databinding.ActivityMainBinding
import com.example.tingting.utils.Entity.User
import com.example.tingting.utils.hide
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Bottom navigation
        val navController = findNavController(R.id.nav_host_fragment_login)
//
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.getOrCreateBadge(R.id.chat).apply {
            number = 10
            isVisible = true
        }



        binding.ivAvatar.setOnClickListener {
            // Call intent to setting
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        //Notification
        binding.imageView.setOnClickListener {
            val intent = Intent(this, Notifycation::class.java)
            startActivity(intent)
        }

        val user = FirebaseAuth.getInstance().uid
        val mRef = FirebaseDatabase.getInstance().getReference("/Users/$user")

        val accessToken = AccessToken.getCurrentAccessToken()

        if (accessToken!=null && !accessToken.isExpired) {

            val request = GraphRequest.newMeRequest(
                accessToken,
            ) { _, response ->

                val jsonObject = JSONObject(response?.jsonObject.toString())
                val url = jsonObject.getJSONObject("picture").getJSONObject("data").getString("url")

                mRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val user = p0.getValue(User::class.java)
                        if (user?.avatar == "") {
                            mRef.child("avatar").setValue(url)
                        }
                    }

                })
            }

            val parameters = Bundle()
            parameters.putString("fields", "id,name,link, picture.type(large)")
            request.parameters = parameters
            request.executeAsync()
        }

        mRef.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user = p0.getValue(User::class.java)

                    if (user != null) {
                        Glide.with(this@MainActivity)
                            .load(user.avatar)
                            .into(binding.ivAvatar)
                    }
                }
            }
        })

    }

    private fun hideStatusBarNavigationBar() {
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT
    }

}
