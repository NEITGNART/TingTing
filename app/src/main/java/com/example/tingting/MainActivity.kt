package com.example.tingting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tingting.databinding.ActivityMainBinding


//import com.example.tingting.databinding.ActivityMainBinding

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


        val navController = findNavController(R.id.fragment)


        
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.firstFragment,
                R.id.secondFragment,
                R.id.thirdFragment,
                R.id.fourFragment
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigationView.setupWithNavController(navController)



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
