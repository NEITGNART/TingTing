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
            binding.tvChatMessage.text = "00.00"
            binding.tvTime.text = "hello"

//            val authID = FirebaseAuth.getInstance().currentUser?.uid
//            val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$authID/${chat.id}")


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


