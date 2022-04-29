package com.example.tingting

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tingting.databinding.ItemChatHistoryBinding
import com.example.tingting.utils.Entity.Chat
import com.example.tingting.utils.hide
import com.example.tingting.utils.invisible
import com.example.tingting.utils.show

class ChatMessageAdapter(
    val context: Context,
    val chats: MutableList<Chat>,
    val layoutParams: FrameLayout.LayoutParams,
    val layoutParams2: FrameLayout.LayoutParams,
    val photoParam: FrameLayout.LayoutParams,
    val photoParam2: FrameLayout.LayoutParams,
    val imgUrl: String,
    val resources: Resources,
) : RecyclerView.Adapter<ChatMessageAdapter.ViewHolder>() {
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(badge:Chat ) {
            val binding = ItemChatHistoryBinding.bind(view)

            if (badge.type == "Message") {
                binding.tvMessage.text = badge.text
            } else if (badge.type == "Voice Message") {
                binding.tvVoiceMessage.text = badge.text
            }


            binding.tvMessage.hide()
            binding.tvVoiceMessage.hide()
            binding.cardPhoto.hide()
            if (badge.isSender) {
                when (badge.type) {
                    "Message" -> {
                        binding.tvMessage.show()

                        binding.tvMessage.layoutParams = layoutParams2
                        binding.tvMessage.background =
                            resources.getDrawable(R.drawable.da_bg_chat_history_grd)

                        binding.tvMessage.setTextColor(
                            ContextCompat.getColor(
                                context,
                                (R.color.white)
                            )
                        )

                    }
                    "Voice Message" -> {
                        binding.tvVoiceMessage.show()
                        binding.tvVoiceMessage.layoutParams = layoutParams2
                        binding.tvVoiceMessage.background =
                            resources.getDrawable(R.drawable.da_bg_chat_history_grd)
                        binding.tvVoiceMessage.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.da_white
                            )
                        )


                    }
                    else -> {
                        binding.cardPhoto.show()
                        binding.cardPhoto.layoutParams = photoParam2
                        Glide.with(binding.root.context).load(imgUrl).into(binding.ivChatPhoto)
                    }
                }
                binding.ivChatProfile.visibility = View.INVISIBLE
            } else {
                badge.showProfile = true
                when (badge.type) {
                    "Message" -> {
                        binding.tvMessage.show()
                        binding.tvMessage.layoutParams = layoutParams
                        binding.tvMessage.background =
                            resources.getDrawable(R.drawable.da_bg_chat_history)
                        binding.tvMessage.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.da_textColorPrimary
                            )
                        )
                    }
                    "Voice Message" -> {
                        binding.tvVoiceMessage.show()
                        binding.tvVoiceMessage.layoutParams = layoutParams
                        binding.tvVoiceMessage.background =
                            resources.getDrawable(R.drawable.da_bg_chat_history)
                        binding.tvVoiceMessage.setTextColor(
                            ContextCompat.getColor(context, R.color.da_textColorPrimary)
                        )
                    }
                    else -> {
                        binding.cardPhoto.show()
                        binding.cardPhoto.layoutParams = photoParam
                        Glide.with(binding.root.context).load(imgUrl).into(binding.ivChatPhoto)
                    }
                }
                Glide.with(context).load(imgUrl).into(binding.ivChatProfile)
                if (badge.showProfile) {
                    binding.ivChatProfile.show()
                } else {
                    binding.ivChatProfile.invisible()
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatHistoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chats[position]
        holder.bind(chat)
    }

    override fun getItemCount() = chats.size
}


