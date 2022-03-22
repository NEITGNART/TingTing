package com.example.tingting

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.tingting.databinding.ActivityMainBinding


//import com.example.tingting.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    var button: Button?= null
    lateinit var  adapters: Adapters
    lateinit var viewpager: ViewPager2
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var list = mutableListOf<Int>()


        list.add(R.drawable.image1)
        list.add(R.drawable.image2)
        list.add(R.drawable.image3)

        adapters = Adapters(this)
        adapters.setContentList(list)
        viewpager = findViewById(R.id.viewPagerMain)
        viewpager.adapter = adapters

    }
}
