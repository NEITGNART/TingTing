package com.example.tingting

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.tingting.databinding.DaMapMarkerBinding
import com.example.tingting.utils.applyColorFilter
import com.example.tingting.utils.color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator


class MarkerClusterRenderer(
    private var context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<DAMapMarker>
) : DefaultClusterRenderer<DAMapMarker>(context, map, clusterManager) {   // 1

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
        }
        markerOptions?.title(item.title)
    }

    override fun onClusterItemRendered(clusterItem: DAMapMarker?, marker: Marker?) {
        super.onClusterItemRendered(clusterItem, marker)

        if (!clusterItem?.isUser!!) {
            Glide.with(context)
                .asBitmap()
                .load(clusterItem.mUser?.avatar)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val x =getMarkerBitmapFromView(resource, false, context)
                        marker?.setIcon(BitmapDescriptorFactory.fromBitmap(x))
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
        } else {
            marker?.setIcon(
                BitmapDescriptorFactory.fromBitmap(
                    setCurrentLocationIcon()!!
                )
            )
        }

    }

    private fun setCurrentLocationIcon(): Bitmap? {
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
            image: Bitmap, isSelected: Boolean,
            context: Context
        ): Bitmap {


            val customMarkerView =
                (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                    R.layout.da_map_marker,
                    null
                )

            val ivUserImage = customMarkerView.findViewById<ImageView>(R.id.ivUserImage)
            ivUserImage.setImageBitmap(image)

            val viewBack = customMarkerView.findViewById<ImageView>(R.id.viewBack)

            val binding = DaMapMarkerBinding.bind(customMarkerView)

            if (!isSelected) {
                viewBack.applyColorFilter(context.color(R.color.da_white))
                viewBack.applyColorFilter(context.color(R.color.da_white))
            } else {
                // ivUserImage bordercolor
                viewBack.applyColorFilter(context.color(R.color.da_red))
                binding.ivUserImage.borderColor = context.color(R.color.da_red)
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