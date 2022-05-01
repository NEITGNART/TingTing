package com.example.tingting.activity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tingting.activity.NotificationAdapter.ViewHolder
import com.example.tingting.databinding.DaItemNotificationBinding
import com.example.tingting.utils.Entity.Notification
import com.google.firebase.database.FirebaseDatabase

class NotificationAdapter(val context: Context, val chats: MutableList<Notification>) :
    RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = DaItemNotificationBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(chats[position])
    }

    override fun getItemCount() = chats.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(noti: Notification) {
            val binding = DaItemNotificationBinding.bind(itemView)

            val ref = FirebaseDatabase.getInstance().getReference("/Users/${noti.id}")


            ref.child("avatar").get().addOnSuccessListener {
                Glide.with(context).load(it.value.toString()).into(binding.ivUser)
            }
            binding.tvUserName.text = noti.content

            noti.time.let {
                val time = it.toLong()
                val date = java.util.Date(time)
                val sdf = java.text.SimpleDateFormat("HH:mm")
                binding.tvTime.text = sdf.format(date)
            }
        }

    }

}
