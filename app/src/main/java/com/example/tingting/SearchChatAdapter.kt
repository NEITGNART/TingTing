package com.example.tingting

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tingting.databinding.DaItemSearchBinding
import com.example.tingting.utils.Entity.User

class SearchChatAdapter(val context: Context, val chats: MutableList<User>) : RecyclerView.Adapter<SearchChatAdapter.ViewHolder>() {
    inner class ViewHolder(val view: ViewGroup) : RecyclerView.ViewHolder(view) {
        fun bind(chat: User) {
            val binding = DaItemSearchBinding.inflate(LayoutInflater.from(context), view, false)
            Glide.with(context).load(chat.avatar).into(binding.ivUser)
            binding.tvUserName.text= chat.name

            binding.root.setOnClickListener {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("toUser", chat)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DaItemSearchBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chats[position]
        holder.bind(chat)
    }

    override fun getItemCount() = chats.size

}
