package com.example.tingting

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
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
import kotlinx.coroutines.launch


private const val TAG = "ThridFragment"

class ThridFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentThridBinding
    lateinit var clusterManager: ClusterManager<DAMapMarker>
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
                Toast.makeText(
                    binding.root.context,
                    "${location.latitude} ${location.longitude}",
                    Toast.LENGTH_SHORT
                )
                    .show()

                googleMap.clear()
                userLatLng = LatLng(location.latitude, location.longitude)
                moveTOLocation(googleMap)
                userLatLng?.let {
                    setUpCircle(it, googleMap)
                }
            } else {
                val locationManager =
                    this.requireActivity()
                        .getSystemService(Context.LOCATION_SERVICE) as LocationManager
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0f
                ) {
                    userLatLng = LatLng(it.latitude, it.longitude)

                    userLatLng?.let {
                        googleMap.clear()
                        setUpCircle(it, googleMap)
                        clusterManager.addItem(
                            DAMapMarker(
                                latlng = it,
                                isUser = true
                            )
                        )
                    }
                }
            }
        }
    }

    private fun setUpCircle(it: LatLng, googleMap: GoogleMap) {
        lifecycleScope.launch {
            val ref = FirebaseDatabase.getInstance()
                .getReference("/Setting/${FirebaseAuth.getInstance().currentUser?.uid}/distance")
            ref.child("min").get().addOnSuccessListener { space ->
                val distance = space.value.toString().toDouble() * 1000
                drawRedCircleLocation(googleMap, it, distance)
                clusterManager.addItem(
                    DAMapMarker(
                        latlng = it,
                        isUser = true
                    )
                )
            }
            ref.child("max").get().addOnSuccessListener { space ->
                val distance = space.value.toString().toDouble() * 1000
                drawRedCircleLocation(googleMap, it, distance)
                clusterManager.addItem(
                    DAMapMarker(
                        latlng = it,
                        isUser = true
                    )
                )
            }
        }

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
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
        }
    }


    private fun setUpClusterManager(googleMap: GoogleMap) {
        clusterManager = ClusterManager<DAMapMarker>(binding.root.context, googleMap)
        val reRender = MarkerClusterRenderer(binding.root.context, googleMap, clusterManager)

//        clusterManager.setOnClusterItemClickListener {
//            if (it.isUser) {
//                return@setOnClusterItemClickListener true
//            }
//
//            binding.rlInfo.show()
//
//            // check that rlInfo is visible
//            if (binding.rlInfo.visibility == View.VISIBLE) {
//                // if it is visible, hide it
//                Glide.with(binding.root.context)
//                    // optimization size
//                    .load(it.mUser?.avatar)
//                    .apply(RequestOptions().override(100, 100))
//                    .into(binding.ivProfile)
//            } else {
//
//            }
//
//            binding.fbBack.onClick {
//                binding.rlInfo.hide()
//            }
//            lastSelected = it
//            return@setOnClusterItemClickListener true
//        }
        clusterManager.renderer = reRender
        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnMarkerClickListener(clusterManager)

        lifecycleScope.launch {
            getItems()
        }
    }

    private fun getItems() {

        val ref = FirebaseDatabase.getInstance().reference.child("Users")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e("Explore frag", "onCancelled: " + p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {

                    val users = p0.children
                    users.forEachIndexed { i: Int, it: DataSnapshot ->
                        Log.i("hihi", it.toString())
                        val user = it.getValue(User::class.java)
                        user?.let {
                            if (it.id != FirebaseAuth.getInstance().currentUser?.uid &&
                                it.address != null
                            ) {
                                user.address?.let {
                                    clusterManager.addItem(
                                        DAMapMarker(
                                            LatLng(
                                                it.latitude,
                                                it.longitude
                                            ), user
                                        )
                                    )
                                }
                            }
                            Log.e("Explore frag", "onDataChange: " + it.id)
                        }

                    }
                }
            }
        })

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

        binding.ivFilter.setOnClickListener {
            val action = ThridFragmentDirections.actionBrowserToFilterFragment2()
            Navigation.findNavController(it).navigate(action)
        }
    }

}

