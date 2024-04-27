package com.example.lab4_android_media_player

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class Mp3Adapter(private val mp3Files: List<File>) :
    RecyclerView.Adapter<Mp3Adapter.Mp3ViewHolder>() {

    class Mp3ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMp3Name: TextView = itemView.findViewById(R.id.item_mp3)
        val mp3PlayButton: ImageButton = itemView.findViewById(R.id.play_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Mp3ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mp3, parent, false)
        return Mp3ViewHolder(view)
    }

    override fun onBindViewHolder(holder: Mp3ViewHolder, position: Int) {
        holder.tvMp3Name.text = mp3Files[position].name
        holder.mp3PlayButton.setOnClickListener {
            // Play the mp3 file
            val intent = Intent(holder.itemView.context, MusicPlayerService::class.java).apply {
                putExtra("data", mp3Files[position].toUri().toString())
            }
            ContextCompat.startForegroundService(holder.itemView.context, intent)
        }
    }

    override fun getItemCount() = mp3Files.size
}