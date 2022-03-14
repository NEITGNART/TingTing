package com.example.tingting

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.squareup.picasso.Picasso


//import com.example.tingting.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    lateinit var  adapters: Adapters
    lateinit var viewpager: ViewPager2
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
