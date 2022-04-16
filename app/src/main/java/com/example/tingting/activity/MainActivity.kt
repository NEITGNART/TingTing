package com.example.tingting.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.tingting.R
import com.example.tingting.SettingActivity
import com.example.tingting.databinding.ActivityMainBinding
import com.example.tingting.utils.Entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    //  hddjwedhd
//    var button: Button?= null
//    lateinit var  adapters: Adapters
//    lateinit var viewpager: ViewPager2
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
//        val toolbar = binding.toolbar
//        setSupportActionBar(toolbar)

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

        val user = FirebaseAuth.getInstance().uid
        val mRef = FirebaseDatabase.getInstance().getReference("/Users/$user")

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



//        var list = mutableListOf<Int>()


//        list.add(R.drawable.image1)
//        list.add(R.drawable.image2)
//        list.add(R.drawable.image3)
//
//        adapters = Adapters(this)
//        adapters.setContentList(list)
//        viewpager = findViewById(R.id.viewPagerMain)
//        viewpager.adapter = adapters

    }


}
