package com.example.tingting

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tingting.databinding.ItemMatchBinding
import com.example.tingting.utils.Entity.User

class MatchesAdapter(
    val context: Context,
    val users: List<User?>,
    val layoutParams: CoordinatorLayout.LayoutParams,
    val layoutParams2: CoordinatorLayout.LayoutParams,
    val width: Int,
) : RecyclerView.Adapter<MatchesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = View.inflate(context, R.layout.item_match, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        user?.let { holder.bind(it, position) }
    }

    override fun getItemCount() = users.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemMatchBinding.bind(view)

        fun bind(user: User, i: Int) {
            binding.tvName.text = user.name
            binding.ivProfile.layoutParams = layoutParams
            binding.ivImg.layoutParams = layoutParams2
            binding.ivImg.setPadding(width / 10, width / 10, width / 10, width / 10)
            if (i % 3 == 1) {
                binding.viewDummy.visibility = View.VISIBLE
            } else {
                binding.viewDummy.visibility = View.GONE
            }
            Glide.with(binding.root.context).load("${user.avatar}").into(binding.ivProfile)

            binding.root.setOnClickListener {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("toUser", user)
                context.startActivity(intent)
            }
        }

    }
}
