package net.cattaka.android.learnscopedstorage

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.cattaka.android.learnscopedstorage.adapter.OperationInfoAdapter
import net.cattaka.android.learnscopedstorage.data.OperationInfo
import net.cattaka.android.learnscopedstorage.data.OperationTarget
import net.cattaka.android.learnscopedstorage.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var adapter: OperationInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        adapter = OperationInfoAdapter(prepareItems().toMutableList())

        binding.recyclerView.apply {
            this.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            this.adapter = this@MainActivity.adapter
        }
    }

    private fun prepareItems(): List<OperationInfo> {
        val items = mutableListOf<OperationInfo>()
        val photoDirectPath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path}/photo.png"
        val audioDirectPath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).path}/audio.mp3"
        val movieDirectPath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).path}/movie.mp4"
        val downloadDirectPath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path}/download.txt"
        items.add(OperationInfo(
                "Photo direct",
                photoDirectPath,
                OperationTarget.IMAGE
        ))
        items.add(OperationInfo(
                "Audio direct",
                audioDirectPath,
                OperationTarget.AUDIO
        ))
        items.add(OperationInfo(
                "Movie direct",
                movieDirectPath,
                OperationTarget.MOVIE
        ))
        items.add(OperationInfo(
                "Download direct",
                downloadDirectPath,
                OperationTarget.DOWNLOAD
        ))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val photoMediaStoreUri = MediaStore.setRequireOriginal(Uri.fromFile(File(photoDirectPath))).toString()
            val audioMediaStoreUri = MediaStore.setRequireOriginal(Uri.fromFile(File(audioDirectPath))).toString()
            val movieMediaStoreUri = MediaStore.setRequireOriginal(Uri.fromFile(File(movieDirectPath))).toString()
            val downloadMediaStoreUri = MediaStore.setRequireOriginal(Uri.fromFile(File(downloadDirectPath))).toString()
            items.add(OperationInfo(
                    "Photo MediaStore Uri",
                    photoMediaStoreUri,
                    OperationTarget.IMAGE
            ))
            items.add(OperationInfo(
                    "Audio MediaStore Uri",
                    audioMediaStoreUri,
                    OperationTarget.AUDIO
            ))
            items.add(OperationInfo(
                    "Movie MediaStore Uri",
                    movieMediaStoreUri,
                    OperationTarget.MOVIE
            ))
            items.add(OperationInfo(
                    "Photo MediaStore Uri",
                    downloadMediaStoreUri,
                    OperationTarget.DOWNLOAD
            ))
        }

        return items
    }
}
