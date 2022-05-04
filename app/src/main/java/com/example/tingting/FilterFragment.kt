package com.example.tingting

import android.R
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.tingting.databinding.FragmentFilterBinding
import com.example.tingting.utils.Entity.LatLng
import com.example.tingting.utils.onClick
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FilterFragment : Fragment() {

    private lateinit var binding: FragmentFilterBinding
    private var dialog: BottomSheetDialog? = null
    private val distanceArray =
        arrayOf(
            "5 km",
            "10 km",
            "15 km",
            "20 km",
            "25 km",
            "30 km",
            "35 km",
            "40 km",
            "45 km",
            "50 km"
        )
    private val ageArray2 = arrayOf("18", "19", "20", "21", "22", "23", "24", "25")
    private val spDisplay = arrayOf("Male", "Female", "All")
    private val userID = FirebaseAuth.getInstance().uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentFilterBinding.inflate(inflater)
        binding.btnBack.onClick {
            Navigation.findNavController(binding.root).navigateUp()
        }
        binding.ivBack.setOnClickListener {
            Navigation.findNavController(it).navigateUp()
        }
        val listOfCheckBox = ArrayList<CheckBox>()
        listOfCheckBox.add(binding.cbDisney)
        listOfCheckBox.add(binding.cbMusic)
        listOfCheckBox.add(binding.cbgame)
        listOfCheckBox.add(binding.cblife)
        listOfCheckBox.add(binding.cblovecat)
        listOfCheckBox.add(binding.cbwatchtv)
        listOfCheckBox.add(binding.rbNetflix)
        listOfCheckBox.add(binding.rbcungkn)
        val rootRef = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().uid
        var favo: String? = null
        val messageRef2 = rootRef.child("Setting").child(user.toString()).child("favorite")

        messageRef2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    if (favo == null)
                        favo = ds.getValue().toString()
                    else
                        favo = favo + "," + ds.getValue().toString()

                }
                Log.i("hahahahaha", favo.toString())

                if (favo != null) {
                    val list_favo: List<String> = favo!!.split(",")
                    listOfCheckBox.forEach { checkbox ->
                        if (getSinglefavo(checkbox.text.toString(), list_favo)) {
                            checkbox.setChecked(true);
                            checkbox.setBackgroundResource(com.example.tingting.R.drawable.gradient_border)
                            checkbox.setTextColor(resources.getColor(com.example.tingting.R.color.da_red1))

                        }

                    }
                }
                listOfCheckBox.forEach { checkbox ->
                    checkbox.setOnClickListener {
                        if (checkbox.isChecked) {
                            checkbox.setBackgroundResource(com.example.tingting.R.drawable.gradient_border)
                            checkbox.setTextColor(resources.getColor(com.example.tingting.R.color.da_red1))
                        } else {
                            checkbox.setBackgroundResource(com.example.tingting.R.drawable.gray_border)
                            checkbox.setTextColor(resources.getColor(com.example.tingting.R.color.black_80))
                        }
                    }
                }
                binding.btnApply.setOnClickListener {
                    var message1: String? = null
                    val userId = FirebaseAuth.getInstance().uid!!
                    FirebaseDatabase.getInstance().getReference("/Setting/$userId/favorite")
                        .removeValue()
                    listOfCheckBox.forEach { checkbox ->
                        if (checkbox.isChecked) {
                            if (message1 == null)
                                message1 = checkbox.getText().toString()
                            else
                                message1 += "," + checkbox.getText().toString()
                        }
                    }
                    message1 = if (message1 == null) "You select nothing" else {
                        "$message1"
                    }
                    Log.i("messss", "$message1")
                    if (message1 != null) {

                        val list_favo1: List<String> = message1!!.split(",")
                        for (i in list_favo1.indices) {
                            val name = list_favo1[i];
                            FirebaseDatabase.getInstance()
                                .getReference("/Setting/$userId/favorite/$name").setValue(name)


                        }
                    }
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


            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("TAG", databaseError.message)
            }
        })

        binding.rangebarDistance.tickTopLabels = distanceArray

        FirebaseDatabase.getInstance().getReference("Setting/$userID").child("distance")
            .child("max").get().addOnSuccessListener { itMax ->
                FirebaseDatabase.getInstance().getReference("Setting/$userID").child("distance")
                    .child("min").get().addOnSuccessListener { itMin ->
                        if (itMin.value == null || itMax.value == null) {
                            binding.rangebarDistance.setRangePinsByValue(
                                0F, 9F
                            )
                        } else {

                            val min: Float = itMin.getValue(Float::class.java)!!
                            val max: Float = itMax.getValue(Float::class.java)!!

                            binding.rangebarDistance.setRangePinsByValue(
                                min, max
                            )
                        }
                    }
            }

        binding.rangebarAge.tickTopLabels = ageArray2
        FirebaseDatabase.getInstance().getReference("Setting/$userID").child("age").child("max")
            .get().addOnSuccessListener { itMax ->
                FirebaseDatabase.getInstance().getReference("Setting/$userID").child("age")
                    .child("min")
                    .get().addOnSuccessListener { itMin ->
                        if (itMin.value == null || itMax.value == null) {
                            binding.rangebarAge.setRangePinsByValue(
                                0F, 7F
                            )
                        } else {
                            val min: Float = itMin.getValue(Float::class.java)!!
                            val max: Float = itMax.getValue(Float::class.java)!!

                            binding.rangebarAge.setRangePinsByValue(
                                min, max
                            )

                        }
                    }
            }

        binding.tvLocation.onClick {
        }


        FirebaseDatabase.getInstance()
            .getReference("/Users/${FirebaseAuth.getInstance().currentUser!!.uid}/address").get()
            .addOnSuccessListener {

                if (it.exists()) {
                    val latlng = it.getValue(LatLng::class.java)!!
                    val geocoder = Geocoder(binding.root.context)
                    val addresses =
                        geocoder.getFromLocation(latlng!!.latitude, latlng!!.longitude, 1)
                    val address = addresses.get(0).getAddressLine(0)
                    val list_address: List<String> = address!!.split(", ")
                    var address_user: String = list_address[2]
                    for (i in 3 until list_address.size) {
                        address_user = address_user + ", " + list_address[i]
                    }
                    binding.tvLocation.setText(address_user)
                }
            }

        val adapter = ArrayAdapter(
            binding.root.context,
            R.layout.simple_spinner_dropdown_item, spDisplay
        )
        binding.spDisplay.adapter = adapter
        FirebaseDatabase.getInstance().getReference("Users/$userID").child("display").get()
            .addOnSuccessListener {
                when (it.value.toString()) {
                    "Male" -> binding.spDisplay.setSelection(0)
                    "Female" -> binding.spDisplay.setSelection(1)
                    "All" -> binding.spDisplay.setSelection(2)
                }
            }

        return binding.root
    }

    private fun getSinglefavo(S: String, list_favo: List<String>): Boolean {
        // pick multiple images

        for (i in list_favo.indices) {
            if (S == list_favo[i]) {
                return true
            }
        }
        return false
    }

}
