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
import com.example.tingting.databinding.ActivityMainBinding
import com.example.tingting.databinding.UserInfoFragmentBinding
import com.example.tingting.utils.hide
import com.google.firebase.storage.FirebaseStorage
import java.text.ParseException
import java.text.SimpleDateFormat
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
        //tv.setVisibility(View.INVISIBLE );
        binding = UserInfoFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        list.add(R.drawable.da_img16)
        list.add(R.drawable.da_img10)
        list.add(R.drawable.da_img12)


        val mainBinding = ActivityMainBinding.inflate(layoutInflater)
        mainBinding.bottomNavigationView.hide()

        // take the root view


        var adapters = Adapters(binding.root.context)
        adapters.setContentList(list)
        var viewpager = binding.viewPagerMain
        viewpager.adapter = adapters
        viewModel = ViewModelProvider(this)[UserInfoViewModel::class.java]

        binding.ivBack.setOnClickListener {
            Navigation.findNavController(binding.root).navigateUp()
        }
        val amount: String = UserInfoFragmentArgs.fromBundle(requireArguments()).name


//        val rootRef = FirebaseDatabase.getInstance().reference

//        val messageRef = rootRef.child("Users")

//        val valueEventListener = object : ValueEventListener {
//
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                for (ds in dataSnapshot.children) {
//
//                    val id = ds.child("id").getValue(String::class.java)
//
//                    if (id == amount) {
//                        val name_user = ds.child("name").getValue(String::class.java)
//                        binding.item.tvName.setText(name_user)
//
//                    }
//
//                }
//            }
//
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.d("TAG", databaseError.getMessage()) //Don't ignore errors!
//            }
//        }
//        messageRef.addListenerForSingleValueEvent(valueEventListener)


        viewModel = ViewModelProvider(this)[UserInfoViewModel::class.java]

        viewModel.getUser(amount).observe(viewLifecycleOwner) { user ->
            // parse DD/MM/YYYY get age
            val dateString = user.birthDate

            dateString?.let {
                try {
                    val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateString)
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    val age = calendar.get(Calendar.YEAR)
                    binding.item.tvName.text = "${user.name} $age"
                } catch (e: ParseException) {
                    binding.item.tvName.text = "${user.name}"
                    e.printStackTrace()
                }
            }
            binding.item.tvLocation.text = user.address
            binding.item.tvDetail.text = user.description
        }


        // Upload image on firebase storage.
//        getMultiImage()
//        getSingleImage()
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