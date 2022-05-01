package com.example.tingting

import android.app.DatePickerDialog
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import com.example.tingting.databinding.FragmentProfileBinding
import com.example.tingting.utils.Entity.LatLng
import com.example.tingting.utils.applyColorFilter
import com.example.tingting.utils.onClick
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)


        //////////////////////////////////////////////////////////////////////////
        val userId = FirebaseAuth.getInstance().uid!!
        var gender: String? = null;


        FirebaseDatabase.getInstance().getReference("/Users/$userId/about").get().addOnSuccessListener {
            if(it.value == null)
                binding.edtAboutMe.setText("")
            else
                binding.edtAboutMe.setText(it.value.toString())
        }
        FirebaseDatabase.getInstance().getReference("/Users/$userId/work").get().addOnSuccessListener {
            if(it.value == null)
                binding.edtWork.setText("")
            else
                binding.edtWork.setText(it.value.toString())
        }
        FirebaseDatabase.getInstance().getReference("/Users/$userId/birthDate").get().addOnSuccessListener {
                binding.edtBirthday.setText(it.value.toString())
        }
        FirebaseDatabase.getInstance().getReference("/Users/$userId/name").get().addOnSuccessListener {
            binding.edtUsername.setText(it.value.toString())
        }

        FirebaseDatabase.getInstance().getReference("/Users/$userId/gender").get().addOnSuccessListener {
            gender = it.value.toString()
            if(gender == "Male"){
                binding.ivMale.background = resources.getDrawable(R.drawable.da_circle_redprimary)
                binding.ivMale.applyColorFilter( resources.getColor(R.color.da_white))
            }
            if(gender == "Female"){
                binding.ivFemale.background = resources.getDrawable(R.drawable.da_circle_redprimary)
                binding.ivFemale.applyColorFilter( resources.getColor(R.color.da_white))
            }
            if(gender == "Other"){
                binding.ivOther.background = resources.getDrawable(R.drawable.da_circle_redprimary)
                binding.ivOther.applyColorFilter( resources.getColor(R.color.da_white))
            }
        }
        binding.ivFemale.onClick {
            gender = "Female"
            binding.ivMale.background = resources.getDrawable(R.drawable.da_bg_gray)
            binding.ivMale.applyColorFilter( resources.getColor(R.color.da_textColorSecondary))
            binding.ivOther.background = resources.getDrawable(R.drawable.da_bg_gray)
            binding.ivOther.applyColorFilter( resources.getColor(R.color.da_textColorSecondary))
            applyColorFilter(resources.getColor(R.color.da_white))
            background =    resources.getDrawable(R.drawable.da_circle_redprimary)

        }
        binding.ivMale.onClick {
            gender = "Male"
            binding.ivFemale.background = resources.getDrawable(R.drawable.da_bg_gray)
            binding.ivFemale.applyColorFilter(resources.getColor(R.color.da_textColorSecondary))
            binding.ivOther.background = resources.getDrawable(R.drawable.da_bg_gray)
            binding.ivOther.applyColorFilter(resources.getColor(R.color.da_textColorSecondary))
            applyColorFilter(resources.getColor(R.color.da_white))
            background =   resources.getDrawable(R.drawable.da_circle_redprimary)
        }
        binding.ivOther.onClick {
            gender = "Other"
            binding.ivMale.background = resources.getDrawable(R.drawable.da_bg_gray)
            binding.ivMale.applyColorFilter( resources.getColor(R.color.da_textColorSecondary))
            binding.ivFemale.background = resources.getDrawable(R.drawable.da_bg_gray)
            binding.ivFemale.applyColorFilter(resources.getColor(R.color.da_textColorSecondary))
            applyColorFilter(resources.getColor(R.color.da_white))
            background =   resources.getDrawable(R.drawable.da_circle_redprimary)
        }

        var address: String? = null;
        var city: String? = null;

        FirebaseDatabase.getInstance().getReference("/Users/$userId/address").get().addOnSuccessListener {
            val latlng = it.getValue(LatLng::class.java)
            val geocoder = Geocoder(binding.root.context)
            val addresses = geocoder.getFromLocation(latlng!!.latitude,latlng!!.longitude, 1)
            address = addresses[0].getAddressLine(0)
            city = addresses[0].locality
            binding.edtLocation.setText(address.toString())
        }


        //////////////////////////////////////////////////////////////////////////////////////

        binding.btnSave.setOnClickListener{
            val builder1: AlertDialog.Builder = AlertDialog.Builder(binding.root.context)
            builder1.setMessage("Bạn muốn lưu thay đổi")
            builder1.setCancelable(true)

            builder1.setPositiveButton(
                "Đồng ý",
            ) { dialog, id ->
                FirebaseDatabase.getInstance().getReference("/Users/$userId/about").setValue( binding.edtAboutMe.text.toString())
                FirebaseDatabase.getInstance().getReference("/Users/$userId/gender").setValue( gender.toString())

                dialog.cancel()
            }

            builder1.setNegativeButton(
                "Hủy"

            ) { dialog, id ->
                dialog.cancel() }

            val alert11: AlertDialog = builder1.create()
            alert11.show()
        }

        binding.ivBack.setOnClickListener {
            Navigation.findNavController(it).navigateUp()
        }

        return binding.root
    }



}