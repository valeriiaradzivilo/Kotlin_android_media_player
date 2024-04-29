package com.example.lab4_android_media_player

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class VideoAdapter(private val videoFiles: List<File>) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvVideoName: TextView = itemView.findViewById(R.id.item_video)
        val videoView: VideoView = itemView.findViewById(R.id.video_view)
        val videoPlayButton: ImageButton = itemView.findViewById(R.id.play_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.tvVideoName.text = videoFiles[position].name
        holder.videoView.setVideoURI(Uri.fromFile(videoFiles[position]))
        holder.videoPlayButton.setOnClickListener {
            if (holder.videoView.isPlaying) {
                holder.videoView.pause()
                holder.videoPlayButton.setImageResource(android.R.drawable.ic_media_play)
            } else {
                holder.videoView.start()
                holder.videoPlayButton.setImageResource(android.R.drawable.ic_media_pause)
            }
        }
    }

    override fun getItemCount() = videoFiles.size
}