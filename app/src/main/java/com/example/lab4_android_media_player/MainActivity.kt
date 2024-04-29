package com.example.lab4_android_media_player

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.ContentResolver
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private val fileList = mutableListOf<File>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                // Check if the permission is granted
                if (isGranted) {
                    // Show a toast message for permission granted

                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show()
                } else {
                    // Show a toast message asking the user to grant the permission
                    Toast.makeText(this, "Please grant permission", Toast.LENGTH_LONG).show()

                }
            }
        // Check if the Android version is TIRAMISU or newer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Use the requestPermessionLauncher to request the READ_MEDIA_IMAGES permission
            requestPermissionLauncher.launch(READ_MEDIA_VIDEO)
            requestPermissionLauncher.launch(READ_MEDIA_AUDIO)
        } else {
            // For older Android versions, use READ_EXTERNAL_STORAGE permission
            requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE)
            requestPermissionLauncher.launch(WRITE_EXTERNAL_STORAGE)
        }

        val progressBar: ProgressBar = findViewById(R.id.progressBar)


        val bottomAppBar: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomAppBar.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.nav_music) {
                progressBar.visibility = View.VISIBLE
                fileList.clear()
                fileList.addAll(getAllMp3Files(contentResolver).map { File(it) })
                progressBar.visibility = View.GONE
                recyclerView.adapter = Mp3Adapter(fileList)
            } else {
                progressBar.visibility = View.VISIBLE
                scanForMedia("storage/emulated/0/", this)
                fileList.clear()
                fileList.addAll(getAllVideoFiles(contentResolver).map { File(it) })
                progressBar.visibility = View.GONE
                recyclerView.adapter = VideoAdapter(fileList)

            }
            return@setOnItemSelectedListener true
        }



        bottomAppBar.selectedItemId = R.id.nav_music


    }

    private fun getAllMp3Files(contentResolver: ContentResolver): List<String> {
        val mp3Files = mutableListOf<String>()

        // Define the columns you want to retrieve
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA
        )

        // Query MediaStore for audio files
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER

        val mediaCursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )

        mediaCursor?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val path = cursor.getString(pathColumn)

                // You can filter by file extension if needed
                if (name.endsWith(".mp3", ignoreCase = true)) {
                    mp3Files.add(path)
                }
            }
        }

        return mp3Files
    }

    private fun getAllVideoFiles(contentResolver: ContentResolver): List<String> {
        val videoFiles = mutableListOf<String>()

        // Define the columns you want to retrieve
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATA
        )

        // Query MediaStore for video files
        val videoCursor = contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null
        )

        videoCursor?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val path = cursor.getString(pathColumn)

                // You can filter by file extension if needed
                if (name.endsWith(".mp4", ignoreCase = true) ||
                    name.endsWith(".mov", ignoreCase = true)
                ) {
                    videoFiles.add(path)
                }
            }
        }

        // Log the number of video files retrieved
        Log.d(TAG, "Number of video files: ${videoFiles.size}")

        return videoFiles
    }

    private fun scanForMedia(filePath: String, context: Context) {
        MediaScannerConnection.scanFile(context, arrayOf(filePath), null) { _, uri ->
            Log.d(TAG, "Media scan complete: $uri")
        }
    }


}