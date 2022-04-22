package com.example.tingting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.tingting.databinding.FragmentFourBinding
import com.example.tingting.utils.Entity.User
import com.example.tingting.utils.setVerticalLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FourFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FourFragment : Fragment() {

    private lateinit var binding: FragmentFourBinding
    private val chats = mutableListOf<User>()
    private val currentUser = FirebaseAuth.getInstance().uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFourBinding.inflate(inflater)
        binding.tvMatches.setOnClickListener {
            val action = FourFragmentDirections.actionChatToFragmentMatches()
            Navigation.findNavController(binding.root).navigate(action)
        }

        // get list of chats from firebase realtime database

        // reference to the database
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Matched/$currentUser")

        myRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: com.google.firebase.database.DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                chats.clear()
                for (i in p0.children) {
                    val user = i.getValue(User::class.java)

                    Log.i("FourFragment", "User ${user}")
                    Log.i("FourFragment", "Current ${currentUser}")

                    if (user != null && user.id != currentUser) {
                        chats.add(user)
                    }
                }
                binding.rvChat.adapter = ChatAdapter(binding.root.context, chats)
            }
        })

        binding.rvChat.apply {
            setVerticalLayout()
        }

        return binding.root
    }

}

