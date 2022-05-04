package com.example.tingting

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.tingting.activity.LoginActivity
import com.example.tingting.databinding.FragmentAppIntroductionBinding
import com.example.tingting.utils.Entity.LatLng
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AppIntroduction : Fragment() {

    private lateinit var binding: FragmentAppIntroductionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAppIntroductionBinding.inflate(layoutInflater)
        binding.btnAccess.setOnClickListener{
            try {
                val fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(this.requireActivity())

                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
//                        Toast.makeText(
//                            binding.root.context,
//                            "${location.latitude} ${location.longitude}",
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
                        FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child("address")
                            .setValue(LatLng(location.latitude, location.longitude))

                    } else {

                        try {
                            val locationManager =
                                this.requireActivity()
                                    .getSystemService(Context.LOCATION_SERVICE) as LocationManager
                            locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                0,
                                0f
                            ) {

                                FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .child("address")
                                    .setValue(LatLng(it.latitude, it.longitude))
                            }
                        } catch (e: SecurityException) {
                        }
                    }
                }

            } catch (e: SecurityException) {
            }
            val action = AppIntroductionDirections.actionAppIntroductionToInputUserName()
            Navigation.findNavController(binding.root).navigate(action)
        }

        binding.btnClose.setOnClickListener{
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }


        return binding.root
    }
}
