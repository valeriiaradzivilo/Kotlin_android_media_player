package com.example.lab4_android_media_player

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class OpenLinkViewerActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var urlInput: EditText
    private lateinit var playButton: ImageButton
    private lateinit var playStopMediaButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.open_link_activity)

        videoView = findViewById(R.id.video_from_link_view)
        urlInput = findViewById(R.id.link_edit_field)
        playButton = findViewById(R.id.play_internet_link_btn)
        playStopMediaButton = findViewById(R.id.play_stop_media_link_button)

        playButton.setOnClickListener {
            if (urlInput.text.toString().isNotEmpty()) {
                videoView.visibility = VideoView.GONE
                playStopMediaButton.visibility = Button.INVISIBLE

                val url = urlInput.text.toString()
                if (url.endsWith(".mp4")) {
                    playVideo(url)
                } else if (url.endsWith(".mp3")) {
                    playAudio(url)
                }
            }
        }
    }

    /// https://cdn.pixabay.com/video/2018/01/31/14035-254146872_large.mp4

    private fun playVideo(url: String) {
        videoView.visibility = VideoView.VISIBLE
        videoView.setVideoURI(Uri.parse(url))
        videoView.setOnPreparedListener {
            it.start()
        }
        playStopMediaButton.visibility = Button.VISIBLE
        playStopMediaButton.text = getString(R.string.pause)
        playStopMediaButton.setOnClickListener {
            if (videoView.isPlaying) {
                videoView.pause()
                playStopMediaButton.text = getString(R.string.play)
            } else {
                videoView.start()
                playStopMediaButton.text = getString(R.string.pause)
            }
        }
    }

    ///https://file-examples.com/storage/fe121d443b662e6a8a224ff/2017/11/file_example_MP3_2MG.mp3

    private fun playAudio(url: String) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                start()
                playStopMediaButton.visibility = Button.VISIBLE
                playStopMediaButton.text = getString(R.string.pause)
                playStopMediaButton.setOnClickListener {
                    if (isPlaying) {
                        pause()
                        playStopMediaButton.text = getString(R.string.play)
                    } else {
                        start()
                        playStopMediaButton.text = getString(R.string.pause)
                    }
                }
            }
            prepareAsync()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}