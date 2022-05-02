package com.example.tingting

import android.R
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
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


        binding.rangebarDistance.tickTopLabels = distanceArray

        FirebaseDatabase.getInstance().getReference("Setting/$userID").child("distance").child("max").get().addOnSuccessListener { itMax ->
            FirebaseDatabase.getInstance().getReference("Setting/$userID").child("distance")
                .child("min").get().addOnSuccessListener { itMin ->
                    if (itMin.value == null || itMax.value == null){
                        binding.rangebarDistance.setRangePinsByValue(
                            0F, 9F)
                    } else {

                        val min: Float = itMin.getValue(Float::class.java)!!
                        val max: Float = itMax.getValue(Float::class.java)!!

                        binding.rangebarDistance.setRangePinsByValue(
                            min, max)
                    }
                }
        }

        binding.rangebarAge.tickTopLabels = ageArray2
        FirebaseDatabase.getInstance().getReference("Setting/$userID").child("age").child("max").get().addOnSuccessListener { itMax ->
            FirebaseDatabase.getInstance().getReference("Setting/$userID").child("age").child("min")
                .get().addOnSuccessListener { itMin ->
                    if (itMin.value == null || itMax.value == null){
                        binding.rangebarAge.setRangePinsByValue(
                            0F, 7F)
                    } else {
                        val min: Float = itMin.getValue(Float::class.java)!!
                        val max: Float = itMax.getValue(Float::class.java)!!

                        binding.rangebarAge.setRangePinsByValue(
                            min, max)

                    }
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
        FirebaseDatabase.getInstance().getReference("Users/$userID").child("display").get().addOnSuccessListener {
            when (it.value.toString()) {
                "Male" -> binding.spDisplay.setSelection(0)
                "Female" -> binding.spDisplay.setSelection(1)
                "All" -> binding.spDisplay.setSelection(2)
            }
        }

        binding.btnApply.setOnClickListener {
            val reference = FirebaseDatabase.getInstance().getReference("/Setting/$userID")
            reference.child("age").child("max")
                .setValue(binding.rangebarAge.rightIndex)
            reference.child("age").child("min")
                .setValue(binding.rangebarAge.leftIndex)
            reference.child("distance").child("max")
                .setValue(binding.rangebarDistance.rightIndex)
            reference.child("distance").child("min")
                .setValue(binding.rangebarDistance.leftIndex)
            FirebaseDatabase.getInstance().getReference("Users/$userID").child("display")
                .setValue(binding.spDisplay.selectedItem.toString())
            Navigation.findNavController(binding.root).navigateUp()
        }

        return binding.root
    }
}
