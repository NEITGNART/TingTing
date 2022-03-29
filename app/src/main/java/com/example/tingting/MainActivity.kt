package com.example.tingting

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.example.tingting.databinding.ActivityMainBinding

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
        val navController = findNavController(R.id.nav_host_fragment)

//        binding.bottomNavigationView.setupWithNavController(navController)
//        binding.bottomNavigationView.getOrCreateBadge(R.id.chat).apply {
//            number = 10
//            isVisible = true
//        }

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
