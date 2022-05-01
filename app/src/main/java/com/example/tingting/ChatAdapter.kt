package com.example.tingting

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.Navigation
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
    val chats: MutableList<User>,
    val matched: Boolean
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(chat: User) {
            val binding = ItemChatBinding.bind(view)

            Glide.with(context).load(chat.avatar).into(binding.ivUser)
            binding.tvUserName.text = chat.name

            FirebaseDatabase.getInstance()
                .getReference("/latest-messages/${chat.id}/${FirebaseAuth.getInstance().uid}")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        Log.d("ChatAdapter", "onCancelled")
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val message = p0.getValue(Chat::class.java)
                        if (message != null) {
                            binding.tvChatMessage.text = message.text

                            message.time.let {
                                val time = it.toLong()
                                val date = java.util.Date(time)
                                val sdf = java.text.SimpleDateFormat("HH:mm")
                                binding.tvTime.text = sdf.format(date)
                            }

                        }
                    }
                })


//            val authID = FirebaseAuth.getInstance().currentUser?.uid
//            val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$authID/${chat.id}")


            if (matched) {
                binding.root.setOnClickListener {
                    val action = SearchChatFragmentDirections.actionSearcMatchedListToUserInfoFragment(chat.id!!)
                    Navigation.findNavController(binding.root).navigate(action)
                }
            } else {
                binding.root.setOnClickListener {
                    val intent = Intent(binding.root.context, ChatActivity::class.java)
                    intent.putExtra("toUser", chat)

                    startActivity(binding.root.context, intent, null)
                }
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


