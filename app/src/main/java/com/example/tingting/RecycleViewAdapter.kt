package com.example.tingting

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tingting.utils.inflate


class RecycleViewAdapter<T>(
    private val layout: Int,
    val onBind: (view: View, item: T, position: Int) -> Unit
) : RecyclerView.Adapter<RecycleViewAdapter<T>.ViewHolder<T>>() {
    private val items = ArrayList<T>()
    var onItemClick: ((pos: Int, view: View, item: T) -> Unit)? = null

    fun addItem(item: T) {
        this.items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun getItem(item: Int): T {
        return this.items[item]
    }

    fun remove(item: T) {
        this.items.remove(item)
        notifyDataSetChanged()
    }

    fun addItems(items: List<T>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun clearItems() {
        this.items.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder<T>(
        private val view: View,
        val onBind: (view: View, item: T, position: Int) -> Unit
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {
        override fun onClick(p0: View?) {
            onItemClick?.invoke(adapterPosition, p0!!, items[adapterPosition])
        }

        fun bind(item: T, position: Int) {
            view.setOnClickListener(this)
            onBind(view, item, position)
        }

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder<T> {
        return ViewHolder(p0.inflate(layout), onBind)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder<T>, pos: Int) {
        holder.bind(items[pos], pos)
    }

}
