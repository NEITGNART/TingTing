package com.example.tingting.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tingting.databinding.ActivityNotifycationBinding
import com.example.tingting.utils.Entity.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotifycationBinding
    private var chats = mutableListOf<Notification>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotifycationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvNotification.apply {
            layoutManager = LinearLayoutManager(this@NotificationActivity)
        }

        FirebaseDatabase.getInstance().getReference("/Notify/${FirebaseAuth.getInstance().uid}")
            .addChildEventListener(object :
                ChildEventListener {

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val noti = p0.getValue(Notification::class.java)
                    if (noti != null) {
                        chats.add(noti)
                        // push reverse
                        binding.rvNotification.adapter = NotificationAdapter(
                            binding.root.context,
                            chats.reversed() as MutableList<Notification>
                        )
                    }

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })



        binding.ivBack.setOnClickListener {
            finish()
        }

    }
}