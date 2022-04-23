package com.example.tingting

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tingting.MarkerClusterRenderer.Companion.getMarkerBitmapFromView
import com.example.tingting.databinding.DaFragmentExploreNearlyBinding
import com.example.tingting.databinding.FragmentThridBinding
import com.example.tingting.utils.Entity.User
import com.example.tingting.utils.color
import com.example.tingting.utils.onClick
import com.example.tingting.utils.show
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import okhttp3.internal.notifyAll

class ThridFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: DaFragmentExploreNearlyBinding
    lateinit var clusterManager: ClusterManager<DAMapMarker>

    private var lastSelected: DAMapMarker? = null
    private var latlngs = arrayListOf(
        LatLng(11.9, 108.9),
        LatLng(12.2, 109.9),
        LatLng(13.1, 110.9),
        LatLng(13.2, 111.9)
    )


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

        googleMap.addCircle(
            CircleOptions()
                .center(LatLng(10.886720, 106.651512))
                .radius(100.0)
                .strokeWidth(0f)
                .fillColor(binding.root.context.color(R.color.da_red_light))
        )

        googleMap.addCircle(
            CircleOptions()
                .center(LatLng(10.886720, 106.651512))
                .radius(300.0)
                .strokeWidth(0f)
                .fillColor(binding.root.context.color(R.color.da_red_light))
        )
        binding.fabChat.onClick {
        }
    }

    private fun moveTOLocation(googleMap: GoogleMap?) {
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(10.886720, 106.651512),
                15f
            )
        )
    }

    private fun setUpClusterManager(googleMap: GoogleMap) {
        clusterManager = ClusterManager<DAMapMarker>(binding.root.context, googleMap)
        val randerer = MarkerClusterRenderer(binding.root.context, googleMap, clusterManager)
        clusterManager.setOnClusterItemClickListener {
            if (it.isUser) {
                return@setOnClusterItemClickListener true
            }
            if (lastSelected != null) {
                randerer.getMarker(lastSelected)?.setIcon(
                    BitmapDescriptorFactory.fromBitmap(
                        getMarkerBitmapFromView(
                            lastSelected?.mUser?.avatar!!,
                            false, binding.root.context
                        )
                    )
                )
            }
            randerer.getMarker(it).setIcon(
                BitmapDescriptorFactory.fromBitmap(
                    getMarkerBitmapFromView(
                        it.mUser?.avatar!!,
                        true, binding.root.context
                    )
                )
            )
            binding.rlInfo.show()
            lastSelected = it
            return@setOnClusterItemClickListener true
        }
        clusterManager.renderer = randerer
        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnMarkerClickListener(clusterManager)

        clusterManager.addItem(DAMapMarker(LatLng(10.886720, 106.651512),isUser = true))
        clusterManager.addItems(getItems())
        clusterManager.cluster()
    }

    private fun getItems(): List<DAMapMarker>? {
        var list = ArrayList<DAMapMarker>()
        latlngs.forEachIndexed { i: Int, latLng: LatLng ->
            var user=User()
            user.avatar = "https://image-us.24h.com.vn/upload/3-2019/images/2019-09-24/1569314930-598-shyn-2-1568970705-width600height541.jpg"
            list.add(DAMapMarker(latLng,user))
        }
        return list
    }

//    private fun getItems() {
//            latlngs.forEachIndexed { i: Int, latLng: LatLng ->
//
//                FirebaseDatabase.getInstance().getReference("Users")
//                    .addListenerForSingleValueEvent(object : ValueEventListener {
//                        override fun onCancelled(p0: DatabaseError) {
//                        }
//                        override fun onDataChange(p0: DataSnapshot) {
//                            val user =  p0.children.iterator().next().getValue(User::class.java)
//                            val marker = DAMapMarker(latLng, user!!)
//                            clusterManager.addItem(marker)
//                            if (i == latlngs.size - 1) {
//                                clusterManager.cluster()
//                            }
//                        }
//                    })
//            }
//    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DaFragmentExploreNearlyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapAddress) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

}

class DAMapMarker(
    latlng: LatLng,
    user: User? = null, isUser: Boolean = false
) : ClusterItem {
    private val mPosition: LatLng = latlng
    private val mTitle: String? = null
    val isUser: Boolean = isUser
    val mSnippet: String? = null
    val mUser: User? = user

    override fun getPosition(): LatLng {
        return mPosition
    }

    override fun getTitle(): String? {
        return mTitle
    }

    override fun getSnippet(): String? {
        return mSnippet
    }

}
