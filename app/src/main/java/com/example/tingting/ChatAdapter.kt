package com.example.tingting

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tingting.databinding.ItemChatBinding
import com.example.tingting.utils.Entity.Chat
import com.example.tingting.utils.Entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.math.log

class ChatAdapter(
    val context: Context,
    val chats: MutableList<User>
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(chat: User) {
            val binding = ItemChatBinding.bind(view)

            Glide.with(context).load(chat.avatar).into(binding.ivUser)
            binding.tvUserName.text = chat.name

            val authID = FirebaseAuth.getInstance().currentUser?.uid
            val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$authID/${chat.id}")

            reference.addChildEventListener(object : ChildEventListener {

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    Log.i("message", p1 + p0.toString())
                    val chatMessage = p0.getValue(Chat::class.java)
                    binding.tvChatMessage.text = chatMessage?.text
                    binding.tvTime.text = chatMessage?.time
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

//            query.get().addOnSuccessListener {
//                Log.i("message1", it.key.toString())
//                FirebaseDatabase.getInstance().getReference("/user-messages/$authID/${chat.id}/${it.key}")
//                    .addValueEventListener(object : ValueEventListener {
//                        override fun onDataChange(dataSnapshot: DataSnapshot) {
//                            val chat = dataSnapshot.getValue(Chat::class.java)
//                            Log.i("message", dataSnapshot.toString())
//                            binding.tvChatMessage.text = chat?.text
//                            binding.tvTime.text = chat?.time
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            TODO("Not yet implemented")
//                        }
//                    })
//            }

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, ChatActivity::class.java)
                intent.putExtra("toUser", chat)

                startActivity(binding.root.context, intent, null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chats[position]
        holder.bind(chat)
    }

    override fun getItemCount() = chats.size
}


