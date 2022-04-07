package com.example.tingting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.tingting.databinding.ItemMatchBinding
import com.example.tingting.utils.Entity.User

class MatchesAdapter(
    val context: Context,
    val users: List<User?>,
) : RecyclerView.Adapter<MatchesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = View.inflate(context, R.layout.item_match, null)
        return  ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user!!)
    }

    override fun getItemCount() = users.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val binding = ItemMatchBinding.bind(view)

        fun bind(user: User) {
            binding.tvName.text = user.name
        }

    }
}
