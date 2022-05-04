package com.example.tingting.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.audiofx.BassBoost
import android.os.Bundle
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationListenerCompat
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.example.tingting.R
import com.example.tingting.SettingActivity
import com.example.tingting.databinding.ActivityMainBinding
import com.example.tingting.utils.Entity.LatLng
import com.example.tingting.utils.Entity.User
import com.example.tingting.utils.hide
import com.example.tingting.utils.show
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Bottom navigation
        val navController = findNavController(R.id.nav_host_fragment_login)

        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)



        navController.addOnDestinationChangedListener { _, destination, _ ->

            if (destination.id == R.id.homepage
                || destination.id == R.id.wholike
                || destination.id == R.id.browser
                || destination.id == R.id.chat
            ) {
                binding.bottomNavigationView.show()
                binding.appbar.show()
            } else {
                binding.bottomNavigationView.hide()
                binding.appbar.hide()
            }

        }




        val reference =
            FirebaseDatabase.getInstance()
                .getReference("/Matched/${FirebaseAuth.getInstance().currentUser!!.uid}")

        // count number of child

        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                // count number of child
                val count = p0.childrenCount
                binding.bottomNavigationView.getOrCreateBadge(R.id.chat).apply {
                    number = count.toInt()
                    isVisible = true
                }
            }

        })


        binding.ivAvatar.setOnClickListener {
            // Call intent to setting
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        //Notification
        binding.ivNoti.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }

        val user = FirebaseAuth.getInstance().uid
        val mRef = FirebaseDatabase.getInstance().getReference("/Users/$user")

        val accessToken = AccessToken.getCurrentAccessToken()

        if (accessToken != null && !accessToken.isExpired) {

            val request = GraphRequest.newMeRequest(
                accessToken,
            ) { _, response ->


                lifecycleScope.launch {
                    val jsonObject = JSONObject(response?.jsonObject.toString())
                    val url =
                        jsonObject.getJSONObject("picture").getJSONObject("data").getString("url")

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
            }

            val parameters = Bundle()
            parameters.putString("fields", "id,name,link, picture.type(large)")
            request.parameters = parameters
            request.executeAsync()
        }


        lifecycleScope.launch {
            mRef.addValueEventListener(object :
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

            FirebaseDatabase.getInstance().getReference("/SeenNotify/$user")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val value = p0.value
                        value?.let {
                            if (it == true) {
                                binding.viewStatus.show()
                            } else {
                                binding.viewStatus.hide()
                            }
                        }
                    }
                })
        }


    }


}
