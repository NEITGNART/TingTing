package com.example.tingting

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.tingting.databinding.ActivityChatBinding
import com.example.tingting.utils.*
import com.example.tingting.utils.Entity.Chat
import com.example.tingting.utils.Entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

private const val TAG = "ChatActivity"
private val PICK_IMAGES_CODE = 0
private lateinit var images: MutableList<Uri?>

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

        binding.tvUsername.text= toUser?.name

        binding.item.ivSend.onClick {
            if (messageType == MESSAGE) {
                performSendMessage()
            } else if (messageType == VOICE_MESSAGE) {
            }
            else if (messageType == MEDIA){
                performSendMedia()
            }

            resetAddLayout()
        }

        binding.item.ivAdd.onClick {

            if (!binding.item.rlAdd.isVisible) {
                setImageResource(R.drawable.da_ic_plus_gradient)
                binding.item.rlAdd.show()
            } else {
                setImageResource(R.drawable.da_ic_add)
                resetAddLayout()
            }
        }

        binding.item.ivGallary.onClick {
            applyColorFilter(color(R.color.da_red))
            binding.item.ivVoice.applyColorFilter(color(R.color.da_textColorSecondary))
            binding.item.ivGif.applyColorFilter(color(R.color.da_textColorSecondary))

            binding.item.llVoice.hide()
            binding.item.rvGif.hide()
            hideSoftKeyboard()
            messageType = MEDIA
            images = mutableListOf()
            pickImageIntent()
        }


        binding.item.ivVoice.onClick {
            binding.item.rvPhoto.hide()
            binding.item.rvGif.hide()

//            binding.item.llVoice.show()
            applyColorFilter(color(R.color.da_red))
            binding.item.ivGallary.applyColorFilter(color(R.color.da_textColorSecondary))
            binding.item.ivGif.applyColorFilter(color(R.color.da_textColorSecondary))
            hideSoftKeyboard()
            messageType = VOICE_MESSAGE
        }
        binding.item.ivGif.onClick {
            applyColorFilter(color(R.color.da_red))
            binding.item.ivGallary.applyColorFilter(color(R.color.da_textColorSecondary))
            binding.item.ivVoice.applyColorFilter(color(R.color.da_textColorSecondary))
            binding.item.rvPhoto.hide()
            binding.item.llVoice.hide()
//            binding.item.rvGif.show()
            hideSoftKeyboard()
            messageType = MEDIA

        }

    }

//    private fun setSelectedImg(imageView: ImageView?) {
//        binding.item.ivGallary.applyColorFilter(color(R.color.da_textColorSecondary))
//        binding.item.ivVoice.applyColorFilter(color(R.color.da_textColorSecondary))
//        binding.item.ivGif.applyColorFilter(color(R.color.da_textColorSecondary))
//        imageView?.apply {
//            applyColorFilter(color(R.color.da_red))
//        }
//    }


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

        val latestMessageRef =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }

    private fun performSendMedia() {
        // how do we actually send a message to firebase...
        val text = binding.item.edtSearch.text.toString()

        if (images.isNullOrEmpty()) {
            return
        } else {
            var imageStorage = FirebaseStorage.getInstance().getReference("/Images")
            val fromId = FirebaseAuth.getInstance().uid
            val toId = toUser?.id!!
            if (fromId == null) return

            for (image in images){
                val reference =
                    FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
                val toReference =
                    FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

                val chatMessage = Chat(
                    id = reference.key!!,
                    text = "",
                    fromId = fromId,
                    toId = toId,
                    isSender = true,
                    time = "${System.currentTimeMillis()}",
                    type = MEDIA,
                    showProfile = false
                )

                val imageRef = imageStorage.child(UUID.randomUUID().toString())
                imageRef.putFile(image!!)
                    .addOnSuccessListener {
                        imageRef.downloadUrl.addOnSuccessListener {
                            chatMessage.text = it.toString()

                            reference.setValue(chatMessage)
                                .addOnSuccessListener {
                                    Log.d(TAG, "Saved our chat message: ${reference.key}")
                                    binding.item.edtSearch.text.clear()
                                    binding.rvChat.adapter?.notifyDataSetChanged()
                                    binding.rvChat.scrollToPosition(chats.size - 1)
                                }

                            toReference.setValue(chatMessage)
                        }
                    }
                    .addOnFailureListener {
                        Log.i("UserInfo", "Upload fail")
                    }
            }
        }
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

    private fun pickImageIntent(){
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image(s)"), PICK_IMAGES_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGES_CODE || requestCode == Activity.RESULT_OK) {

            if (data != null) {
                if (data!!.clipData != null) {
                    val count: Int = data.clipData!!.itemCount
                    for (i in 0 until count) {
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        images!!.add(imageUri)
                    }
                } else {
                    val imageUri = data!!.data
                    images!!.add(imageUri)
                }
            }
        }
    }
}






