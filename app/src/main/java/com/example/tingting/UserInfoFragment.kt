package com.example.tingting

import android.R
import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.tingting.databinding.ActivityMainBinding
import com.example.tingting.databinding.UserInfoFragmentBinding
import com.example.tingting.utils.Global.getDistance
import com.example.tingting.utils.hide
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
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
            val action = UserInfoFragmentDirections.actionUserInfoFragmentToHomepage()
            Navigation.findNavController(binding.root).navigate(action)
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


            lifecycleScope.launch {
                val id_user = user.id
                var favo : String? = null
                val messageRef2 = rootRef.child("Setting").child(amount).child("favorite")
                messageRef2.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (ds in dataSnapshot.children) {
                            if(favo ==null)
                                favo= ds.getValue().toString()
                            else
                                favo = favo +", "+ds.getValue().toString()

                        }
                        if(favo==null) {
                            binding.item.tvfavorite.visibility = View.GONE
                            binding.item.txtFavorite.visibility = View.GONE
                        }
                        else
                            binding.item.tvfavorite.setText(favo)


                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.d("TAG", databaseError.message)
                    }
                })
                FirebaseDatabase.getInstance().getReference("/Users/$id_user/work").get().addOnSuccessListener {
                    if(it.value == null)
                        binding.item.tvProfession.visibility = View.GONE
                    else
                        binding.item.tvProfession.setText(it.value.toString())
                }
                FirebaseDatabase.getInstance().getReference("/Users/$id_user/about").get().addOnSuccessListener {
                    if(it.value == null)
                        binding.item.tvDetail.visibility = View.GONE
                    else
                        binding.item.tvDetail.setText(it.value.toString())
                }
                var address: String? = null;

                FirebaseDatabase.getInstance().getReference("/Users/$id_user/address").get().addOnSuccessListener { it ->
                    val latlng = it.getValue(com.example.tingting.utils.Entity.LatLng::class.java)
                    if(latlng != null) {
                        val geocoder = Geocoder(binding.root.context)
                        val addresses =
                            geocoder.getFromLocation(latlng!!.latitude, latlng!!.longitude, 1)
                        address = addresses[0].getAddressLine(0)
                        // get city name, state name, country name

                        // addresses[0].getAddressLine(0) // add

                        binding.item.tvLocation.setText(address.toString())
                        val userId = FirebaseAuth.getInstance().uid!!
                        FirebaseDatabase.getInstance().getReference("/Users/$userId/address").get().addOnSuccessListener {
                            val latlng_user = it.getValue(com.example.tingting.utils.Entity.LatLng::class.java)
                            binding.item.tvDisatance.text= getDistance(latlng!!.latitude, latlng!!.longitude,latlng_user!!.latitude, latlng_user!!.longitude).toString() + " Km"

                        }
                    }
                    else{
                        binding.item.tvLocation.visibility = View.GONE
                        binding.item.txtLocation.visibility = View.GONE

                    }
                }
            }

            binding.item.tvLang.text = user.birthDate
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