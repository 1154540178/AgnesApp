package com.agnes.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agnes.app.databinding.ItemImageBinding
import com.bumptech.glide.Glide

class ImageAdapter(
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    data class ImageItem(val url: String, val prompt: String)

    private val images = mutableListOf<ImageItem>()

    fun addImage(item: ImageItem) {
        images.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        images.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount() = images.size

    inner class ImageViewHolder(private val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(images[position].url)
                }
            }
        }

        fun bind(item: ImageItem) {
            binding.tvImageTitle.text = item.prompt
            Glide.with(binding.ivImage.context)
                .load(item.url)
                .centerCrop()
                .into(binding.ivImage)
        }
    }
}
