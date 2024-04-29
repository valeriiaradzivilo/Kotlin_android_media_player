package com.example.lab4_android_media_player.adapters

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.lab4_android_media_player.R
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
        val mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(holder.itemView.context, mp3Files[position].toUri())
            prepare()
            holder.mp3PlayButton.setOnClickListener {


                if (holder.mp3PlayButton.drawable.constantState == ContextCompat.getDrawable(
                        holder.itemView.context,
                        android.R.drawable.ic_media_pause
                    )?.constantState
                ) {
                    stop()

                    holder.mp3PlayButton.setImageResource(android.R.drawable.ic_media_play)
                } else {
                    start()

                    holder.mp3PlayButton.setImageResource(android.R.drawable.ic_media_pause)
                }

            }
        }
    }

    override fun getItemCount() = mp3Files.size
}