package com.example.tingting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import com.example.tingting.databinding.ActivityChatBinding
import com.example.tingting.utils.Entity.Chat
import com.example.tingting.utils.Entity.User
import com.example.tingting.utils.getDisplayWidth
import com.example.tingting.utils.hide
import com.example.tingting.utils.onClick
import com.example.tingting.utils.setVerticalLayout

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private var chats = mutableListOf<Chat>()


    private var messageType: String = MESSAGE
    private var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.START
        var layoutParams2 = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams2.gravity = Gravity.END
        binding.ivBack.onClick {
            onBackPressed()
        }
        val width = (getDisplayWidth() / 1.5).toInt()
        var photoParam = FrameLayout.LayoutParams(
            width,
            width
        )
        photoParam.gravity = Gravity.START

        var photoParam2 = FrameLayout.LayoutParams(
            width,
            width
        )
        photoParam2.gravity = Gravity.END

        val resource = resources

        chats = getUserChats()

        binding.rvChat.apply {
            setVerticalLayout()
            adapter = ChatMessageAdapter(
                binding.root.context, chats,
                photoParam = photoParam, layoutParams = layoutParams, layoutParams2 = layoutParams2,
                resources = resource, photoParam2 = photoParam2
            )
        }

        // receive chat message from intent
        toUser = intent.getParcelableExtra("toUser")


        binding.item.ivSend.onClick {
            var badge2 = Chat()
            if (messageType == MESSAGE) {
                badge2.chat = binding.item.edtSearch.text.toString()

            } else if (messageType == VOICE_MESSAGE) {
                badge2.chat = "00:12"
            }
            badge2.type = messageType
            badge2.isSender = true
            chats.add(badge2)
            binding.rvChat.adapter?.notifyDataSetChanged()
            binding.item.edtSearch.text.clear()
            binding.rvChat.scrollToPosition(chats.size - 1)
        }

    }




    fun getUserChats(): MutableList<Chat> {
        var list = mutableListOf<Chat>()

        var badge = Chat()
        badge.chat = "Hey Malanie"
        badge.isSender = true
        badge.type = "Message"

        list.add(badge)

        var badge1 = Chat()
        badge1.chat = "Hello"
        badge1.type = "Message"

        list.add(badge1)

        var badge2 = Chat()
        badge2.chat = "Have i bother you?"
        badge2.isSender = true
        badge2.type = "Message"

        list.add(badge2)

        var badge3 = Chat()
        badge3.chat = "N0"
        badge3.type = "Message"

        list.add(badge3)

        var badge4 = Chat()
        badge4.chat = "Just Say it"
        badge4.type = "Message"

        badge4.showProfile = false

        list.add(badge4)
        return list
    }

    private fun resetAddLayout() {
        binding.item.rlAdd.hide()
        binding.item.rvPhoto.hide()
        binding.item.llVoice.hide()
        binding.item.rvGif.hide()
        binding.item.ivAdd.setImageResource(R.drawable.da_ic_add)
    }


    companion object {
        private var MESSAGE = "Message"
        private var VOICE_MESSAGE = "Voice Message"
        private var MEDIA = "Media"

    }




}






