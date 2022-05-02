package com.example.tingting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView

class Adapters(val context: Context): RecyclerView.Adapter<Adapters.MyViewHolder>() {
    lateinit var list: ArrayList<String>

    fun setContentList(list:ArrayList<String> ){
        this.list = list
        notifyDataSetChanged()
    }

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        var image = itemView.findViewById<RoundedImageView>(R.id.list_item_image)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_image, parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
      Glide.with(context).load(list[position]).into(holder.image)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}