package com.example.tingting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tingting.databinding.FragmentSecondBinding


class SecondFragment : Fragment() {
    private lateinit var binding: FragmentSecondBinding
    private var searches = mutableListOf<User>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSecondBinding.inflate(layoutInflater)

        binding.rvList.layoutManager = GridLayoutManager(context, 2)

        generateList()

        binding.rvList.adapter = UserAdapter(binding.root.context, searches)


        return binding.root
    }

    fun generateList() {
        searches.apply {
            addUser {
                name = "Dezaa"; img = R.drawable.da_user_profile; distance = "2 km"; proffesion =
                "Art Director";age = "25"
            }

            addUser {
                name = "Rose"; img = R.drawable.da_img7; distance =
                "2 km"; proffesion = "Art Director";age = "22"
            }
            addUser {
                name = "Spohia"; img = R.drawable.da_img8; distance = "2 km"; proffesion =
                "Art Director";age = "20"
            }
            addUser {
                name = "Stella"; img = R.drawable.da_img9; distance = "2 km"; proffesion =
                "Art Director";age = "18"
            }
            addUser {
                name = "Sam"; img = R.drawable.da_img10; distance = "2 km"; proffesion =
                "Art Director";age = "25"
            }
            addUser {
                name = "Tiffany"; img = R.drawable.da_img11; distance = "2 km"; proffesion =
                "Art Director";age = "21"
            }
            addUser {
                name = "Dezaa"; img = R.drawable.da_img12; distance = "2 km"; proffesion =
                "Art Director";age = "25"
            }

            addUser {
                name = "Rose"; img = R.drawable.da_img14; distance =
                "2 km"; proffesion = "Art Director";age = "22"
            }
            addUser {
                name = "Spohia"; img = R.drawable.da_img15; distance = "2 km"; proffesion =
                "Art Director";age = "20"
            }
            addUser {
                name = "Stella"; img = R.drawable.da_img16; distance = "2 km"; proffesion =
                "Art Director";age = "18"
            }
            addUser {
                name = "Sam"; img = R.drawable.da_img17; distance = "2 km"; proffesion =
                "Art Director";age = "25"
            }


        }
    }

    fun MutableList<User>.addUser(block: User.() -> Unit) {
        add(User().apply(block))
    }





}


class User {
    var name:String?=null
    var distance:String?=null
    var age:String?=null
    var proffesion:String?=null
    var isOnline:Boolean=false
    var img:Int?=null

}