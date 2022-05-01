package com.example.tingting

import android.R
import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.tingting.databinding.ActivityMainBinding
import com.example.tingting.databinding.UserInfoFragmentBinding
import com.example.tingting.utils.hide
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
    val list: ArrayList<String> = ArrayList()

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
        val amount: String = UserInfoFragmentArgs.fromBundle(requireArguments()).idTarget
        var adapters = Adapters(binding.root.context)


        val rootRef = FirebaseDatabase.getInstance().reference
        val messageRef1 = rootRef.child("Images").child(amount)
        messageRef1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    list.add(ds.getValue().toString())
                    Log.i("hahahaha", ds.getValue().toString())
                }
                adapters.setContentList(list)
                var viewpager = binding.viewPagerMain
                viewpager.adapter = adapters

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("TAG", databaseError.message)
            }
        })


        val mainBinding = ActivityMainBinding.inflate(layoutInflater)
        mainBinding.bottomNavigationView.hide()


        viewModel = ViewModelProvider(this)[UserInfoViewModel::class.java]



        binding.ivBack.setOnClickListener {
            Navigation.findNavController(binding.root).navigateUp()


        }


        viewModel = ViewModelProvider(this)[UserInfoViewModel::class.java]

        viewModel.getUser(amount).observe(viewLifecycleOwner) { user ->
            // parse DD/MM/YYYY get age
            val dateString = user.birthDate

            binding.item.tvLocation.text = user.name

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
            binding.item.tvProfession.visibility = View.GONE
            binding.item.tvDetail.visibility = View.GONE
            binding.item.tvDetail.text = user.description
            binding.item.tvLang.text = user.birthDate
            user.address?.let {
                fromLatLntToAddress(LatLng(it.latitude,it.longitude))
            }
            if (binding.item.tvLocation.text == "") {
                binding.item.txtLocation.visibility = View.GONE
                binding.item.tvLocation.visibility = View.GONE
            }
        }


        // Upload image on firebase storage.
//        getMultiImage()
//        getSingleImage()
    }

    fun fromLatLntToAddress(lt: LatLng) {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>? = geocoder.getFromLocation(lt.latitude, lt.longitude, 1)
        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            val sb = StringBuilder()
            for (i in 0 until address.maxAddressLineIndex) {
                sb.append(address.getAddressLine(i)).append("\n")
            }
            sb.append(address.locality).append("\n")
            sb.append(address.postalCode).append("\n")
            sb.append(address.countryName)
            binding.item.tvLocation.text = sb.toString()
        }
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
}