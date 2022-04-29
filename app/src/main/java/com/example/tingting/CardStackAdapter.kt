package com.example.tingting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tingting.databinding.FragmentFirstBinding


class CardStackAdapter(
    val binding: FragmentFirstBinding,
    private var spots: List<Spot> = emptyList()
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_spot, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spot = spots[position]
        holder.name.text = "${spot.name}"
        holder.city.text = spot.city

        Glide.with(holder.itemView.context)
            .load(spot.url)
            .apply(RequestOptions().override(300, 300))
            .into(holder.image)

        holder.itemView.setOnClickListener { v ->
            Toast.makeText(v.context, spot.name, Toast.LENGTH_SHORT).show()
            val action =
                FirstFragmentDirections.actionFirstFragmentToUserInfoFragment(idTarget = spot.id_user)
            Navigation.findNavController(holder.name).navigate(action)
        }

    }

    override fun getItemCount(): Int {
        return spots.size
    }

    fun setSpots(spots: List<Spot>) {
        this.spots = spots
    }

    fun getSpots(): List<Spot> {
        return spots
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        var city: TextView = view.findViewById(R.id.item_city)
        var image: ImageView = view.findViewById(R.id.item_image)
    }

}
