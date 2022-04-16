package com.example.tingting

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tingting.databinding.FragmentMatchesBinding
import com.example.tingting.utils.Entity.Matched
import com.example.tingting.utils.Entity.User
import com.example.tingting.utils.getDisplayWidth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class FragmentMatches : Fragment() {
    private lateinit var viewModel: FragmentMatchesMessageViewModel
    private lateinit var binding: FragmentMatchesBinding
    private var matches = mutableListOf<User?>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMatchesBinding.inflate(inflater)
        binding.tvAll.setOnClickListener {
            val action = FragmentMatchesDirections.actionFragmentMatchesToChat()
            Navigation.findNavController(it).navigateUp()
        }

        val width = (requireActivity().getDisplayWidth() / 4.2).toInt()
        var layoutParams= CoordinatorLayout.LayoutParams(width, width)
        layoutParams.bottomMargin=width/8
        var layoutParams2= CoordinatorLayout.LayoutParams((width/2.5).toInt(), (width/2.5).toInt())
        layoutParams2.gravity= Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL

//        viewModel = ViewModelProvider(this)[FragmentMatchesMessageViewModel::class.java]
//        viewModel.matches.observe(viewLifecycleOwner) {
//            matches = it
//        }

        val database = FirebaseDatabase.getInstance().reference
        val match = database.child("Match").child( "${FirebaseAuth.getInstance().uid}").child("matched_list")
        val matched = mutableListOf<Matched>()

        match.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (data in dataSnapshot.children) {
                        // get name key
                        val id = data.key.toString()
                        Log.i("Matched", "$id")

                        val date = data.getValue(String::class.java)
                        matched.add(Matched(id, date))

                        id.let {
                            val FirebaseUser = database.child("Users").child(it!!)
                            FirebaseUser.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val user = dataSnapshot.getValue(User::class.java)
                                    matches.add(user)
                                    binding.rvMatches.adapter = MatchesAdapter(requireContext(), matches, layoutParams, layoutParams2, width)
                                }
                                override fun onCancelled(databaseError: DatabaseError) {
                                    Log.w("Matched", "loadPost:onCancelled", databaseError.toException())
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("Matched", "loadPost:onCancelled", databaseError.toException())
                }
            }
        )


        binding.rvMatches.layoutManager = GridLayoutManager(requireContext(), 3)
        return binding.root
    }
}