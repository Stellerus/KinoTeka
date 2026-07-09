package com.example.kinoteka.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kinoteka.R
import com.example.kinoteka.model.MediaItem
import com.example.kinoteka.model.Movie
import com.example.kinoteka.model.Series

class MediaAdapter(
    private var items: List<MediaItem>,
    private val onItemClick: (MediaItem) -> Unit,
    private val onDeleteClick: (MediaItem) -> Unit
) : RecyclerView.Adapter<MediaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val typeIcon: ImageView = view.findViewById(R.id.type_icon)
        val titleText: TextView = view.findViewById(R.id.title_text)
        val subtitleText: TextView = view.findViewById(R.id.subtitle_text)
        val deleteButton: ImageView = view.findViewById(R.id.delete_button)
    }

    fun setItems(newItems: List<MediaItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_media, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.titleText.text = item.title

        when (item) {
            is Movie -> {
                holder.typeIcon.setImageResource(R.drawable.ic_movie)
                holder.subtitleText.text = "${item.director}, ${item.year}, ${item.genre}"
            }
            is Series -> {
                holder.typeIcon.setImageResource(R.drawable.ic_series)
                holder.subtitleText.text = "${item.creators}, ${item.years}, ${item.genre}"
            }
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount() = items.size
}
