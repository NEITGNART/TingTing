package com.example.tingting

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tingting.databinding.FragmentThridBinding
import com.example.tingting.utils.Entity.User
import com.example.tingting.utils.color
import com.example.tingting.utils.hide
import com.example.tingting.utils.onClick
import com.example.tingting.utils.show
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val TAG = "ThridFragment"

class ThridFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentThridBinding
    lateinit var clusterManager: ClusterManager<DAMapMarker>

    private var lastSelected: DAMapMarker? = null
    private var userLatLng: LatLng? = null

    override fun onMapReady(googleMap: GoogleMap) {
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    binding.root.context, R.raw.googlemap
                )
            )
            if (!success) {
                Log.e("Explore frag", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("Explore frag", "Can't find style. Error: ", e)
        }

        binding.ivCurrentLocation.onClick {
            moveTOLocation(googleMap)
            Toast.makeText(binding.root.context, "$userLatLng", Toast.LENGTH_SHORT).show()
        }

        // check that the user has granted location permission
        // ask for permission if it hasn't been granted yet

        if (ActivityCompat.checkSelfPermission(
                binding.root.context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }

        lifecycleScope.launch {
            setUpClusterManager(googleMap)
        }
        
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this.requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                Toast.makeText(binding.root.context, "${location.latitude}", Toast.LENGTH_SHORT)
                    .show()
                userLatLng = LatLng(location.latitude, location.longitude)
            }
            moveTOLocation(googleMap)
            userLatLng?.let {
                drawRedCircleLocation(googleMap, it, 100.0)
                drawRedCircleLocation(googleMap, it, 300.0)
                clusterManager.addItem(
                    DAMapMarker(
                        latlng = it,
                        isUser = true
                    )
                )
            }

        }
    }


    // Use when the first time user use google maps
    private fun getCurrentUserLocation() {
        val locationManager =
            this.requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        locationManager.requestLocationUpdates(
//            LocationManager.GPS_PROVIDER,
//            0,
//            0f
//        ) {
//            userLatLng = LatLng(it.latitude, it.longitude)
//        }
    }


    private fun drawRedCircleLocation(googleMap: GoogleMap, latLng: LatLng, radius: Double) {
        googleMap.addCircle(
            CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeWidth(0f)
                .fillColor(binding.root.context.color(R.color.da_red_light))
        )
    }


    private fun moveTOLocation(googleMap: GoogleMap?) {
        userLatLng?.let {
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 16f))
        }
    }


    private fun setUpClusterManager(googleMap: GoogleMap) {
        clusterManager = ClusterManager<DAMapMarker>(binding.root.context, googleMap)
        val reRender = MarkerClusterRenderer(binding.root.context, googleMap, clusterManager)

        clusterManager.setOnClusterItemClickListener {
            if (it.isUser) {
                return@setOnClusterItemClickListener true
            }

            binding.rlInfo.show()

            // check that rlInfo is visible
            if (binding.rlInfo.visibility == View.VISIBLE) {
                // if it is visible, hide it
                Glide.with(binding.root.context)
                    // optimization size
                    .load(it.mUser?.avatar)
                    .apply(RequestOptions().override(100, 100))
                    .into(binding.ivProfile)
            } else {

            }

            binding.fbBack.onClick {
                binding.rlInfo.hide()
            }
            lastSelected = it
            return@setOnClusterItemClickListener true
        }
        clusterManager.renderer = reRender
        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnMarkerClickListener(clusterManager)
        getItems()
    }

    private fun getItems() {

        // get list of firebase users

        val ref = FirebaseDatabase.getInstance().reference.child("Users")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e("Explore frag", "onCancelled: " + p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {

                    val users = p0.children
                    users.forEachIndexed { i: Int, it: DataSnapshot ->
                        val user = it.getValue(User::class.java)
                        if (user?.address != null) {
                            if (user.id == FirebaseAuth.getInstance().currentUser?.uid) {
                                try {
//                                    val latLng = getLatLngFromAddress(user.address)

                                } catch (e: Exception) {
                                    Log.e("Explore frag", "getItems: " + e.message)
                                }

                            } else {
                                val address = user.address
                                try {
                                    val latLng = getLatLngFromAddress(address)
                                    clusterManager.addItem(DAMapMarker(latLng, user))
                                } catch (e: Exception) {
                                    Log.e("Explore frag", "onDataChange: " + e.message)
                                }
                            }
                        }
                    }
                }
            }
        })


    }

    private fun getLatLngFromAddress(address: String?): LatLng {
        val geocoder = Geocoder(binding.root.context)
        val list = geocoder.getFromLocationName(address, 3)
        if (list.size > 0) {
            return LatLng(list[0].latitude, list[0].longitude)
        }
        throw Exception("Can't get latlng from address")
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThridBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapAddress) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

}

