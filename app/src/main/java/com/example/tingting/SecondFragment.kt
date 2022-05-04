package com.example.tingting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.tingting.databinding.FragmentSecondBinding
import com.example.tingting.utils.Entity.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase


class SecondFragment : Fragment() {
    private lateinit var binding: FragmentSecondBinding
    private var searches = mutableListOf<User>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSecondBinding.inflate(layoutInflater)

        binding.rvList.layoutManager = GridLayoutManager(context, 2)

        loadMatchedFromFirebase()

        binding.rvList.adapter = UserAdapter(binding.root.context, searches)


        binding.ivSearch.setOnClickListener {
            val action = SecondFragmentDirections.actionWholikeToSearchChatFragment32(matched = true)
            Navigation.findNavController(binding.root).navigate(action)
        }


        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN or ItemTouchHelper.UP
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
//                Toast.makeText(binding.root.context, "on Move", Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {

                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.adapterPosition

                // check that swiped direction is right

                if (swipeDir == ItemTouchHelper.LEFT || swipeDir == ItemTouchHelper.RIGHT) {
                    // Confirm that the user wants to delete the item
                    // Delete the item from the database

                    val builder1: AlertDialog.Builder = AlertDialog.Builder(binding.root.context)
                    builder1.setMessage("Bạn muốn hủy tương hợp người này")
                    builder1.setCancelable(true)

                    builder1.setPositiveButton(
                        "Đồng ý",
                    ) { dialog, id ->

                        val user = searches[position]

                        FirebaseDatabase.getInstance().getReference("/Matched/${Firebase.auth.uid}/${user.id}").removeValue()
                        FirebaseDatabase.getInstance().getReference("/Matched/${user.id}/${Firebase.auth.uid}").removeValue()

                        FirebaseDatabase.getInstance().getReference("/Visited/${Firebase.auth.uid}/${user.id}").removeValue()
                        FirebaseDatabase.getInstance().getReference("/Visited/${user.id}/${Firebase.auth.uid}").removeValue()

                        FirebaseDatabase.getInstance().getReference("/Match/${Firebase.auth.uid}/${user.id}").removeValue()
                        FirebaseDatabase.getInstance().getReference("/Match/${user.id}/${Firebase.auth.uid}").removeValue()

                        searches.removeAt(position)
                        binding.rvList.adapter?.notifyDataSetChanged()

                        dialog.cancel()
                    }

                    builder1.setNegativeButton(
                        "Hủy"

                    ) { dialog, id ->

                        binding.rvList.adapter?.notifyDataSetChanged()
                        dialog.cancel() }

                    val alert11: AlertDialog = builder1.create()
                    alert11.show()

                }

            }
        }

        ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(binding.rvList)
        return binding.root
    }

    private fun loadMatchedFromFirebase() {
        val userId = Firebase.auth.uid!!
        val ref = FirebaseDatabase.getInstance().getReference("/Matched/$userId")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                searches.clear()
                for (snapshot in dataSnapshot.children) {
                    val matchedId = snapshot.key

                    // get matched user from firebase

                    Log.i("matchedId", matchedId.toString())

                    val matchedRef =
                        FirebaseDatabase.getInstance().getReference("/Users/$matchedId")

                    matchedRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val matched = dataSnapshot.getValue(User::class.java)
                            searches.add(matched!!)
                            binding.rvList.adapter?.notifyDataSetChanged()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Failed to read value
                        }
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        })
    }
}


