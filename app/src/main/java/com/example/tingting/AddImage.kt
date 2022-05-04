package com.example.tingting

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.tingting.activity.MainActivity
import com.example.tingting.databinding.FragmentAddImageBinding
import com.example.tingting.utils.Entity.LatLng
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class AddImage : Fragment() {
    private lateinit var binding: FragmentAddImageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var images: MutableList<Uri?>
    private val PICK_IMAGES_CODE = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = FirebaseAuth.getInstance()

        images = mutableListOf()

        // Inflate the layout for this fragment
        binding = FragmentAddImageBinding.inflate(layoutInflater)

        ///////////////////////////////////////////////////////////////////////
        binding.btnComplete.setOnClickListener{



            if (images.isNullOrEmpty()) {
                Toast.makeText(context, "Please select at least 1 photos", Toast.LENGTH_SHORT)
                    .show()
            }
            else {
                var imageStorage = FirebaseStorage.getInstance().getReference("/Images")
                val userID = FirebaseAuth.getInstance().currentUser?.uid.toString()
                val mDatabaseReference = FirebaseDatabase.getInstance().reference
                mDatabaseReference.child("Users").child(userID).child("firstTimeLogin").setValue(false)

                images.forEachIndexed { index, uri ->
                    val imageRef = imageStorage.child(UUID.randomUUID().toString())
                    imageRef.putFile(uri!!)
                        .addOnSuccessListener {
                            imageRef.downloadUrl.addOnSuccessListener {
                                if (index == 0){
                                    mDatabaseReference.child("Users").child(userID).child("avatar").setValue(it.toString())
                                }
                                mDatabaseReference.child("Images").child(userID).push().setValue(it.toString())
                            }
                        }
                        .addOnFailureListener {
                            Log.i("UserInfo", "Upload fail")
                        }
                }

                val intent = Intent(context, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // can't go back
                startActivity(intent)

            }
        }

        binding.btnBack.setOnClickListener{
            val action = AddImageDirections.actionAddImageToInputfavorite()
            Navigation.findNavController(binding.root).navigate(action)
        }


        var listButtonAddImage =
            arrayListOf(
                binding.ivAddImage1,
                binding.ivAddImage2,
                binding.ivAddImage3,
                binding.ivAddImage4,
                binding.ivAddImage5,
                binding.ivAddImage6)

        listButtonAddImage.forEach { buttonAddImage ->
            buttonAddImage.setOnClickListener{
                pickImageIntent()
            }
        }
        return binding.root
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
            var listButtonAddImage =
                arrayListOf(
                    binding.ivAddImage1,
                    binding.ivAddImage2,
                    binding.ivAddImage3,
                    binding.ivAddImage4,
                    binding.ivAddImage5,
                    binding.ivAddImage6
                )
            images = mutableListOf()
            if (data != null){
                if (data!!.clipData != null) {
                    val count: Int = data.clipData!!.itemCount
                    for (i in 0 until count) {
                        if (i == 6) break
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        images!!.add(imageUri)
                    }
                } else {
                    val imageUri = data!!.data
                    images!!.add(imageUri)
                }

                for (i in 0 until images.size) {
                    listButtonAddImage[i].setImageURI(images[i])
                    listButtonAddImage[i].setBackgroundResource(R.drawable.da_background_tab)
                }
            }
            for (i in images.size until listButtonAddImage.size) {
                listButtonAddImage[i].setImageResource(R.drawable.placeholder_img)
                listButtonAddImage[i].setBackgroundResource(R.drawable.dashed_border)
            }
        }
    }

}