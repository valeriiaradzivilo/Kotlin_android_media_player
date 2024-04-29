package com.example.lab4_android_media_player

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab4_android_media_player.adapters.Mp3Adapter
import com.example.lab4_android_media_player.adapters.VideoAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private var fileList: MutableList<File> = mutableListOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var openLinkViewer: ImageButton

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show()
                initializeMediaContent()
            } else {
                Toast.makeText(this, "Please grant permission", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check and request appropriate permissions
        requestPermissions()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        progressBar = findViewById(R.id.progressBar)
        openLinkViewer = findViewById(R.id.open_link_viewer)

        val bottomAppBar: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomAppBar.setOnItemSelectedListener { item ->
            progressBar.visibility = View.VISIBLE
            when (item.itemId) {
                R.id.nav_music -> loadMusicFiles()
                R.id.nav_videos -> loadVideoFiles()
            }
            true
        }

        bottomAppBar.selectedItemId = R.id.nav_music
        openLinkViewer.setOnClickListener {
            val intent = Intent(this, OpenLinkViewerActivity::class.java)
            startActivity(intent)
        }


    }

    private fun requestPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        permissions.forEach { permission ->
            if (checkSelfPermission(permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun initializeMediaContent() {
        fileList = mutableListOf()
        loadMusicFiles() // Load music files by default
    }

    private fun loadMusicFiles() {
        fileList.clear()
        fileList.addAll(getAllMp3Files(contentResolver).map { File(it) })
        progressBar.visibility = View.GONE
        recyclerView.adapter = Mp3Adapter(fileList)
    }

    private fun loadVideoFiles() {
        fileList.clear()
        fileList.addAll(getAllVideoFiles(contentResolver).map { File(it) })
        progressBar.visibility = View.GONE
        recyclerView.adapter = VideoAdapter(fileList)
    }

    private fun getAllMp3Files(contentResolver: ContentResolver): List<String> {
        val mp3Files = mutableListOf<String>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            while (cursor.moveToNext()) {
                val path = cursor.getString(pathColumn)
                if (path.endsWith(".mp3", ignoreCase = true)) {
                    mp3Files.add(path)
                }
            }
        }
        return mp3Files
    }

    private fun getAllVideoFiles(contentResolver: ContentResolver): List<String> {
        val videoFiles = mutableListOf<String>()
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATA
        )
        contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            while (cursor.moveToNext()) {
                val path = cursor.getString(pathColumn)
                if (path.endsWith(".mp4", ignoreCase = true) ||
                    path.endsWith(".mov", ignoreCase = true)
                ) {
                    videoFiles.add(path)
                }
            }
        }
        Log.d(TAG, "Number of video files: ${videoFiles.size}")
        return videoFiles
    }
}
