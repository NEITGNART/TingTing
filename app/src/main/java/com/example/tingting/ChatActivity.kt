package com.example.tingting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import com.example.tingting.databinding.ActivityChatBinding
import com.example.tingting.utils.Entity.Chat
import com.example.tingting.utils.Entity.ChatMessage
import com.example.tingting.utils.Entity.User
import com.example.tingting.utils.getDisplayWidth
import com.example.tingting.utils.hide
import com.example.tingting.utils.onClick
import com.example.tingting.utils.setVerticalLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

private const val TAG = "ChatActivity"

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private var chats = mutableListOf<Chat>()


    private var messageType: String = MESSAGE
    private var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toUser = intent.getParcelableExtra(getString(R.string.toUser))


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

        listenForMessages()

        binding.rvChat.apply {
            setVerticalLayout()
            adapter = ChatMessageAdapter(
                binding.root.context, chats,
                photoParam = photoParam, layoutParams = layoutParams, layoutParams2 = layoutParams2,
                resources = resource, photoParam2 = photoParam2,
                imgUrl = toUser?.avatar!!,
            )

        }

        binding.tvUserName.text = toUser?.name

        binding.item.ivSend.onClick {
            if (messageType == MESSAGE) {
                performSendMessage()
            } else if (messageType == VOICE_MESSAGE) {
            }
        }

    }


    private fun performSendMessage() {
        // how do we actually send a message to firebase...
        val text = binding.item.edtSearch.text.toString()

        if (text.isEmpty()) {
            return
        }

        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.id!!

        if (fromId == null) return

        val reference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = Chat(
            id = reference.key!!,
            text = text,
            fromId = fromId,
            toId = toId,
            isSender = true,
            time = "${System.currentTimeMillis()}",
            type = MESSAGE,
            showProfile = false
        )

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                binding.item.edtSearch.text.clear()
                binding.rvChat.adapter?.notifyDataSetChanged()
                binding.rvChat.scrollToPosition(chats.size - 1)
            }

        toReference.setValue(chatMessage)
    }


    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.id
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(Chat::class.java)
                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text!!)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        chatMessage.isSender = true
                        chats.add(chatMessage)
                        binding.rvChat.adapter?.notifyDataSetChanged()
                    } else {
                        chatMessage.isSender = false
                        chats.add(chatMessage)
                        binding.rvChat.adapter?.notifyDataSetChanged()
                    }
                } else {

                }
                binding.rvChat.scrollToPosition(chats.size - 1)
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

    }


//    fun getUserChats(): MutableList<Chat> {
//        var list = mutableListOf<Chat>()
//
//        var badge = Chat()
//        badge.text = "Hey Malanie"
//        badge.isSender = true
//        badge.type = "Message"
//
//        list.add(badge)
//
//        var badge1 = Chat()
//        badge1.text = "Hello"
//        badge1.type = "Message"
//
//        list.add(badge1)
//
//        var badge2 = Chat()
//        badge2.text = "Have i bother you?"
//        badge2.isSender = true
//        badge2.type = "Message"
//
//        list.add(badge2)
//
//        var badge3 = Chat()
//        badge3.text = "N0"
//        badge3.type = "Message"
//
//        list.add(badge3)
//
//        var badge4 = Chat()
//        badge4.text = "Just Say it"
//        badge4.type = "Message"
//
//        badge4.showProfile = false
//
//        list.add(badge4)
//        return list
//    }

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






