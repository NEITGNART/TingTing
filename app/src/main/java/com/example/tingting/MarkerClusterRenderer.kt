package com.example.tingting

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.tingting.databinding.DaMapMarkerBinding
import com.example.tingting.utils.applyColorFilter
import com.example.tingting.utils.color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator

class MarkerClusterRenderer(
    context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<DAMapMarker>
) : DefaultClusterRenderer<DAMapMarker>(context, map, clusterManager) {   // 1
    private var context: Context = context

    override fun onBeforeClusterItemRendered(
        item: DAMapMarker?,
        markerOptions: MarkerOptions?
    ) {
        if (item?.isUser!!) {
            markerOptions?.icon(
                BitmapDescriptorFactory.fromBitmap(
                    setCurrentLocationIcon()!!
                )
            )
        } else {

            markerOptions?.icon(
                BitmapDescriptorFactory.fromBitmap(
                    getMarkerBitmapFromView(
                        item.mUser?.avatar!!,
                        false,
                        context
                    )
                )
            )
        }

        markerOptions?.title(item.title)
    }

    fun setCurrentLocationIcon(): Bitmap? {
        val iconGenerator = IconGenerator(context)  // 3
        val markerImageView = ImageView(context)
        markerImageView.layoutParams = ViewGroup.LayoutParams(120, 120)
        markerImageView.setImageResource(R.drawable.da_navigation)
        iconGenerator.setBackground(null)
        iconGenerator.setContentView(markerImageView)
        return iconGenerator.makeIcon()
    }

    companion object {
        fun getMarkerBitmapFromView(
            url: String, isSelected: Boolean,
            context: Context
        ): Bitmap {

            val customMarkerView =
                (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                    R.layout.da_map_marker,
                    null
                )

//            val markerImageView = customMarkerView.findViewById<ImageView>(R.id.ivUserImage)
//            Glide.with(context)
//                .load(url)
//                .into(markerImageView)

            val binding = DaMapMarkerBinding.inflate(
                LayoutInflater.from(context),
            )

            if (!isSelected) {
                binding.viewBack.applyColorFilter(context.color(R.color.da_white))
                binding.viewBack.applyColorFilter(context.color(R.color.da_white))
            } else {
                binding.ivUserImage.borderColor = context.color(R.color.da_red)
                binding.viewBack.applyColorFilter(context.color(R.color.da_red))
            }


            customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            customMarkerView.layout(
                0,
                0,
                customMarkerView.measuredWidth,
                customMarkerView.measuredHeight
            )
            customMarkerView.buildDrawingCache()
            val returnedBitmap = Bitmap.createBitmap(
                customMarkerView.measuredWidth, customMarkerView.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(returnedBitmap)
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
            val drawable = customMarkerView.background
            if (drawable != null)
                drawable!!.draw(canvas)
            customMarkerView.draw(canvas)
            return returnedBitmap
        }

    }

} 