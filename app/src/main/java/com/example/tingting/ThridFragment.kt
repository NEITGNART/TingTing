package com.example.tingting

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tingting.databinding.FragmentThridBinding
import com.example.tingting.utils.Entity.User
import com.example.tingting.utils.color
import com.example.tingting.utils.hide
import com.example.tingting.utils.onClick
import com.example.tingting.utils.show
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

        }
        moveTOLocation(googleMap)
        setUpClusterManager(googleMap)


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

        // get current location
        val locationManager =
            binding.root.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

       val location = if (ActivityCompat.checkSelfPermission(
                binding.root.context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                binding.root.context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // refresh fragment
            this.requireActivity().supportFragmentManager.beginTransaction()
                .detach(this)
                .attach(this)
                .commit()
           null
       } else {
           locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
       }

        if (location != null) {
            userLatLng = LatLng(location.latitude, location.longitude)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng!!, 16f))
        }

        userLatLng?.let {
            drawRedCircleLocation(googleMap, it, 100.0)
            drawRedCircleLocation(googleMap, it, 300.0)
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
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 16f))
        }
    }


    private fun setUpClusterManager(googleMap: GoogleMap) {
        clusterManager = ClusterManager<DAMapMarker>(binding.root.context, googleMap)
        val rerender = MarkerClusterRenderer(binding.root.context, googleMap, clusterManager)

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
        clusterManager.renderer = rerender
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
//                                    clusterManager.addItem(DAMapMarker(latLng, user, isUser = true))
                                    userLatLng?.let {
                                        clusterManager.addItem(
                                            DAMapMarker(
                                                latlng = it,
                                                user = user,
                                                isUser = true
                                            )
                                        )
                                    }
                                } catch (e: Exception) {
                                    Log.e("Explore frag", "getItems: " + e.message)
                                }

                            } else {
                                // from address of user get latlng
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

