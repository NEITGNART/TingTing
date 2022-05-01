package com.example.tingting

import android.R
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.compose.ui.res.stringArrayResource
import androidx.navigation.Navigation
import com.example.tingting.databinding.DaBottomSheetActivityBinding
import com.example.tingting.databinding.FragmentFilterBinding
import com.example.tingting.utils.Entity.LatLng
import com.example.tingting.utils.onClick
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FilterFragment : Fragment() {

    private lateinit var binding: FragmentFilterBinding
    private var dialog: BottomSheetDialog? = null
    private val distanceArray =
        arrayOf("1 km", "2 km", "3 km", "4 km", "5 km", "6 km", "7 km", "8 km", "9 km", "10 km")
    private val ageArray2 = arrayOf("18", "19", "20", "21", "22", "23", "24", "25")
    private val spDisplay = arrayOf("Male", "Female", "All")
    private val userID = FirebaseAuth.getInstance().uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentFilterBinding.inflate(inflater)
        binding.ivBack.setOnClickListener {
            Navigation.findNavController(it).navigateUp()
        }

        var minDistance = 0F
        var maxDistance = 0F

        binding.rangebarDistance.tickTopLabels = distanceArray
        FirebaseDatabase.getInstance().getReference("Setting/$userID").child("distance")
            .child("max").get().addOnSuccessListener { itMax ->
                when (itMax.value.toString()) {
                    "1 km" -> maxDistance = 0F
                    "2 km" -> maxDistance = 1F
                    "3 km" -> maxDistance = 2F
                    "4 km" -> maxDistance = 3F
                    "5 km" -> maxDistance = 4F
                    "6 km" -> maxDistance = 5F
                    "7 km" -> maxDistance = 6F
                    "8 km" -> maxDistance = 7F
                    "9 km" -> maxDistance = 8F
                    "10 km" -> maxDistance = 9F
                }

                FirebaseDatabase.getInstance().getReference("Setting/$userID").child("distance")
                    .child("min").get().addOnSuccessListener { itMin ->
                        when (itMin.value.toString()) {
                            "1 km" -> minDistance = 0F
                            "2 km" -> minDistance = 1F
                            "3 km" -> minDistance = 2F
                            "4 km" -> minDistance = 3F
                            "5 km" -> minDistance = 4F
                            "6 km" -> minDistance = 5F
                            "7 km" -> minDistance = 6F
                            "8 km" -> minDistance = 7F
                            "9 km" -> minDistance = 8F
                            "10 km" -> minDistance = 9F
                        }
                        binding.rangebarDistance.setRangePinsByValue(minDistance, maxDistance)
                    }
            }

        var minAge = 0F
        var maxAge = 0F
        binding.rangebarAge.tickTopLabels = ageArray2
        FirebaseDatabase.getInstance().getReference("Setting/$userID").child("age").child("max")
            .get().addOnSuccessListener { itMax ->
                when (itMax.value.toString()) {
                    "18" -> maxAge = 0F
                    "19" -> maxAge = 1F
                    "20" -> maxAge = 2F
                    "21" -> maxAge = 3F
                    "22" -> maxAge = 4F
                    "23" -> maxAge = 5F
                    "24" -> maxAge = 6F
                    "25" -> maxAge = 7F
                }

                FirebaseDatabase.getInstance().getReference("Setting/$userID").child("age")
                    .child("min")
                    .get().addOnSuccessListener { itMin ->
                        when (itMin.value.toString()) {
                            "18" -> minAge = 0F
                            "19" -> minAge = 1F
                            "20" -> minAge = 2F
                            "21" -> minAge = 3F
                            "22" -> minAge = 4F
                            "23" -> minAge = 5F
                            "24" -> minAge = 6F
                            "25" -> minAge = 7F
                        }
                        binding.rangebarAge.setRangePinsByValue(minAge, maxAge)
                    }
            }


        binding.tvLocation.onClick {
        }
        FirebaseDatabase.getInstance().getReference("/Users/${FirebaseAuth.getInstance().currentUser!!.uid}/address").get().addOnSuccessListener {
            val latlng = it.getValue(LatLng::class.java)!!
            val geocoder = Geocoder(binding.root.context)
            val addresses =
                geocoder.getFromLocation(latlng!!.latitude, latlng!!.longitude, 1)
            val address =addresses.get(0).getAddressLine(0)
            val list_address: List<String> = address!!.split(", ")
            var address_user:String=list_address[2]
            for (i in 3 until list_address.size)
            {
                address_user = address_user +", "+ list_address[i]
            }
            binding.tvLocation.setText(address_user)


        }



        val adapter = ArrayAdapter(
            binding.root.context,
            R.layout.simple_spinner_dropdown_item, spDisplay
        )
        binding.spDisplay.adapter = adapter
        FirebaseDatabase.getInstance().getReference("User/$userID").child("display").get()
            .addOnSuccessListener {
                when (it.value.toString()) {
                    "Male" -> binding.spDisplay.setSelection(0)
                    "Female" -> binding.spDisplay.setSelection(1)
                    "All" -> binding.spDisplay.setSelection(2)
                }
            }

        binding.btnApply.setOnClickListener {
            val reference = FirebaseDatabase.getInstance().getReference("/Setting/$userID")
            reference.child("age").child("max").setValue(ageArray2[binding.rangebarAge.rightIndex])
            reference.child("age").child("min").setValue(ageArray2[binding.rangebarAge.leftIndex])
            reference.child("distance").child("max")
                .setValue(distanceArray[binding.rangebarDistance.leftIndex])
            reference.child("distance").child("min")
                .setValue(distanceArray[binding.rangebarDistance.rightIndex])
            FirebaseDatabase.getInstance().getReference("User/$userID").child("display")
                .setValue(binding.spDisplay.selectedItem.toString())

            Navigation.findNavController(binding.root).navigateUp()
        }

        return binding.root

    }
}
