package com.agnes.app.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.agnes.app.R
import com.agnes.app.databinding.ItemVideoBinding

class VideoAdapter(
    private val onItemClick: (Uri) -> Unit
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    data class VideoItem(val prompt: String, val videoUrl: String)

    private val videos = mutableListOf<VideoItem>()

    fun addVideo(item: VideoItem) {
        videos.add(item)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videos[position])
    }

    override fun getItemCount() = videos.size

    inner class VideoViewHolder(private val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(Uri.parse(videos[position].videoUrl))
                }
            }
        }

        fun bind(item: VideoItem) {
            binding.tvVideoTitle.text = item.prompt
            binding.vvVideo.setVideoPath(item.videoUrl)
            binding.vvVideo.setOnPreparedListener { mp ->
                mp.isLooping = true
                mp.start()
            }
            binding.vvVideo.setOnErrorListener { _, what, extra ->
                true // 忽略错误
            }
        }
    }
}
