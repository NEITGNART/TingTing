package com.example.tingting

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.tingting.databinding.UserInfoFragmentBinding
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class UserInfoFragment : Fragment() {
    lateinit var binding: UserInfoFragmentBinding


    companion object {
        fun newInstance() = UserInfoFragment()
    }

    private lateinit var viewModel: UserInfoViewModel
    lateinit var tv: EditText
    var list = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        list.add(R.drawable.da_img16)
        list.add(R.drawable.da_img10)
        list.add(R.drawable.da_img12)

        //tv.setVisibility(View.INVISIBLE );
        binding = UserInfoFragmentBinding.inflate(layoutInflater)
        var adapters = Adapters(binding.root.context)
        adapters.setContentList(list)
        var viewpager = binding.viewPagerMain
        viewpager.adapter = adapters
        viewModel = ViewModelProvider(this).get(UserInfoViewModel::class.java)

        binding.ivBack.setOnClickListener {
            Navigation.findNavController(binding.root).navigateUp()
        }
////        val args: TindercardstackDirections by navArgs()
//
//
//        val  action =   TindercardstackDirections.actionTindercardstackToUserInfoFragment()
        //tv.setText(action.toString())
        val amount: String = UserInfoFragmentArgs.fromBundle(requireArguments()).name
        binding.abcd.tvName.setText(amount)


        // Upload image on firebase storage.

//        getMultiImage()
//        getSingleImage()

        return binding.root
    }

    private fun getSingleImage() {
        // pick multiple images
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    // get image from fragment 
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // get list of images
            val images = data.clipData
            if (images != null) {
                for (i in 0 until images.itemCount) {
                    val image = images.getItemAt(i)
                    val uri = image.uri
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                    // do something with the bitmap
                    storeImageFirebaseStorage(uri)
                }
            }
        }

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val image = data.getData()
            val bitmap =
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, image)
            // do something with the bitmap
            storeImageFirebaseStorage(image!!)
        }
    }

    fun getImage() {

        // pick multiple images
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, 0)

    }

    fun storeImageFirebaseStorage(selectedImage: Uri) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("gs://tingting-94a57.appspot.com/")

        val imageRef = storageRef.child("images/" + UUID.randomUUID().toString())
        imageRef.putFile(selectedImage)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener {
                    Log.i("UserInfo", "${it}")
                }
            }
            .addOnFailureListener {
                Log.i("UserInfo", "Upload fail")
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }
}