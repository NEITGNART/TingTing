package com.example.tingting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tingting.databinding.ItemListBinding
import com.example.tingting.utils.Entity.User

class UserAdapter(val context: Context, val searches: MutableList<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(user: User) {
            val binding = ItemListBinding.bind(itemView)
            Glide.with(context).load(user.avatar).into(binding.ivImg)
            binding.tvName.text = user.name
//            binding.tvDisatance.text = user.distance
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(searches[position])

    override fun getItemCount() = searches.size

}
