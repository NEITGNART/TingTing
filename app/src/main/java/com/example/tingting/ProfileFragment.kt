package com.example.tingting

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.tingting.databinding.FragmentProfileBinding
import com.example.tingting.utils.Entity.LatLng
import com.example.tingting.utils.Entity.User
import com.example.tingting.utils.applyColorFilter
import com.example.tingting.utils.onClick
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.nio.file.DirectoryIteratorException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var images: ArrayList<Uri>
    private lateinit var listButtonEditImage: ArrayList<ImageView>
    private lateinit var listImage: ArrayList<ImageView>
    private lateinit var listKey: ArrayList<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)


        val userId = FirebaseAuth.getInstance().uid!!
        var gender: String? = null;

        images = arrayListOf(Uri.EMPTY, Uri.EMPTY, Uri.EMPTY, Uri.EMPTY, Uri.EMPTY, Uri.EMPTY)

        listButtonEditImage = arrayListOf(
            binding.ivEdit1,
            binding.ivEdit2,
            binding.ivEdit3,
            binding.ivEdit4,
            binding.ivEdit5,
            binding.ivEdit6
        )

        listImage = arrayListOf(
            binding.ivImage1,
            binding.ivImage2,
            binding.ivImage3,
            binding.ivImage4,
            binding.ivImage5,
            binding.ivImage6
        )

        listKey = arrayListOf("", "", "", "", "", "")

        FirebaseDatabase.getInstance()
            .getReference("/Images/${FirebaseAuth.getInstance().currentUser!!.uid}")
            .addValueEventListener(object :
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: com.google.firebase.database.DataSnapshot) {
                    for ((i, child) in p0.children.withIndex()){
                        if (i == 6) break
                        val url = child.getValue(String::class.java)
                        Glide.with(binding.root.context)
                            .load(url)
                            .into(listImage[i])

                        listButtonEditImage[i].setImageResource(R.drawable.da_ic_mode_edit_black_24dp)
                        listKey[i] = child.key.toString()
                    }

                }

            })


        listImage.forEachIndexed { index, imageView ->
            imageView.onClick {
                pickImageIntent(this, index)
            }
        }

        FirebaseDatabase.getInstance().getReference("/Users/$userId/about").get()
            .addOnSuccessListener {
                if (it.value == null)
                    binding.edtAboutMe.setText("")
                else
                    binding.edtAboutMe.setText(it.value.toString())
            }
        FirebaseDatabase.getInstance().getReference("/Users/$userId/work").get()
            .addOnSuccessListener {
                if (it.value == null)
                    binding.edtWork.setText("")
                else
                    binding.edtWork.setText(it.value.toString())
            }
        FirebaseDatabase.getInstance().getReference("/Users/$userId/birthDate").get()
            .addOnSuccessListener {
                binding.edtBirthday.setText(it.value.toString())
            }
        FirebaseDatabase.getInstance().getReference("/Users/$userId/name").get()
            .addOnSuccessListener {
                binding.edtUsername.setText(it.value.toString())
            }

        FirebaseDatabase.getInstance().getReference("/Users/$userId/gender").get()
            .addOnSuccessListener {
                gender = it.value.toString()
                if (gender == "Male") {
                    binding.ivMale.background =
                        resources.getDrawable(R.drawable.da_circle_redprimary)
                    binding.ivMale.applyColorFilter(resources.getColor(R.color.da_white))
                }
                if (gender == "Female") {
                    binding.ivFemale.background =
                        resources.getDrawable(R.drawable.da_circle_redprimary)
                    binding.ivFemale.applyColorFilter(resources.getColor(R.color.da_white))
                }
                if (gender == "Other") {
                    binding.ivOther.background =
                        resources.getDrawable(R.drawable.da_circle_redprimary)
                    binding.ivOther.applyColorFilter(resources.getColor(R.color.da_white))
                }
            }
        binding.ivFemale.onClick {
            gender = "Female"
            binding.ivMale.background = resources.getDrawable(R.drawable.da_bg_gray)
            binding.ivMale.applyColorFilter(resources.getColor(R.color.da_textColorSecondary))
            binding.ivOther.background = resources.getDrawable(R.drawable.da_bg_gray)
            binding.ivOther.applyColorFilter(resources.getColor(R.color.da_textColorSecondary))
            applyColorFilter(resources.getColor(R.color.da_white))
            background = resources.getDrawable(R.drawable.da_circle_redprimary)

        }
        binding.ivMale.onClick {
            gender = "Male"
            binding.ivFemale.background = resources.getDrawable(R.drawable.da_bg_gray)
            binding.ivFemale.applyColorFilter(resources.getColor(R.color.da_textColorSecondary))
            binding.ivOther.background = resources.getDrawable(R.drawable.da_bg_gray)
            binding.ivOther.applyColorFilter(resources.getColor(R.color.da_textColorSecondary))
            applyColorFilter(resources.getColor(R.color.da_white))
            background = resources.getDrawable(R.drawable.da_circle_redprimary)
        }
        binding.ivOther.onClick {
            gender = "Other"
            binding.ivMale.background = resources.getDrawable(R.drawable.da_bg_gray)
            binding.ivMale.applyColorFilter(resources.getColor(R.color.da_textColorSecondary))
            binding.ivFemale.background = resources.getDrawable(R.drawable.da_bg_gray)
            binding.ivFemale.applyColorFilter(resources.getColor(R.color.da_textColorSecondary))
            applyColorFilter(resources.getColor(R.color.da_white))
            background = resources.getDrawable(R.drawable.da_circle_redprimary)
        }

        var address: String? = null;
        var city: String? = null;

        FirebaseDatabase.getInstance().getReference("/Users/$userId/address").get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val latlng = it.getValue(LatLng::class.java)
                    val geocoder = Geocoder(binding.root.context)
                    val addresses =
                        geocoder.getFromLocation(latlng!!.latitude, latlng!!.longitude, 1)
                    address = addresses[0].getAddressLine(0)
                    city = addresses[0].locality
                    binding.edtLocation.setText(address.toString())
                }
            }


        //////////////////////////////////////////////////////////////////////////////////////

        binding.btnSave.setOnClickListener {
            Log.i("hihi", images.toString())
            Log.i("hihi", listKey.toString())
            val builder1: AlertDialog.Builder = AlertDialog.Builder(binding.root.context)
            builder1.setMessage("Bạn muốn lưu thay đổi")
            builder1.setCancelable(true)

            builder1.setPositiveButton(
                "Đồng ý",
            ) { dialog, id ->
                FirebaseDatabase.getInstance().getReference("/Users/$userId/about")
                    .setValue(binding.edtAboutMe.text.toString())
                FirebaseDatabase.getInstance().getReference("/Users/$userId/gender")
                    .setValue(gender.toString())

                images.forEachIndexed { index, it ->
                    if (it != Uri.EMPTY) {
                        val imageRef = FirebaseStorage.getInstance().getReference("/Images")
                            .child(UUID.randomUUID().toString())
                        imageRef.putFile(it)
                            .addOnSuccessListener {
                                imageRef.downloadUrl.addOnSuccessListener {
                                    if (index == 0) {
                                        FirebaseDatabase.getInstance()
                                            .getReference("Users/${FirebaseAuth.getInstance().uid}/avatar")
                                            .setValue(it.toString())
                                    }
                                    if (listKey[index] == "")
                                        FirebaseDatabase.getInstance()
                                            .getReference("Images/${FirebaseAuth.getInstance().uid}")
                                            .push().setValue(it.toString())
                                    else {
                                        FirebaseDatabase.getInstance()
                                            .getReference("Images/${FirebaseAuth.getInstance().uid}/${listKey[index]}")
                                            .setValue(it.toString())
                                    }
                                }
                            }
                            .addOnFailureListener {
                                Log.i("UserInfo", "Upload fail")
                            }

                    }
                }
                dialog.cancel()
                Navigation.findNavController(binding.root).navigateUp()
            }

            builder1.setNegativeButton(
                "Hủy"

            ) { dialog, id ->
                dialog.cancel()
            }

            val alert11: AlertDialog = builder1.create()
            alert11.show()
        }

        binding.ivBack.setOnClickListener {
            Navigation.findNavController(binding.root).navigateUp()
        }

        return binding.root
    }


    private fun pickImageIntent(buttonEditImage: ImageView, code: Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.ACTION_PICK, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image(s)"), code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("hihi", requestCode.toString() + " " + resultCode.toString())
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val imageUri = data.data
                images[requestCode] = imageUri!!
                listImage[requestCode].setImageURI(images[requestCode])
                listButtonEditImage[requestCode].setImageResource(R.drawable.da_ic_mode_edit_black_24dp)
            }
        }
    }
}