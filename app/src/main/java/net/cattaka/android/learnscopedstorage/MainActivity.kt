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
        val photoDirect = OperationInfo(
                "file:///android_asset/photo.png",
                "Photo",
                "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path}/photo.png",
                OperationTarget.IMAGE
        )
        val audioDirect = OperationInfo(
                "file:///android_asset/audio.ogg",
                "Audio",
                "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).path}/audio.ogg",
                OperationTarget.AUDIO
        )
        val movieDirect = OperationInfo(
                "file:///android_asset/movie.webm",
                "Movie",
                "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).path}/movie.webm",
                OperationTarget.MOVIE
        )
        val downloadDirect = OperationInfo(
                "file:///android_asset/text.txt",
                "Download",
                "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path}/text.txt",
                OperationTarget.DOWNLOAD
        )
        items.add(photoDirect)
        items.add(audioDirect)
        items.add(movieDirect)
        items.add(downloadDirect)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (info in arrayOf(photoDirect, audioDirect,movieDirect,downloadDirect)) {
                val mediaStoreUri = MediaStore.setRequireOriginal(Uri.fromFile(File(info.path.get()!!))).toString()
                items.add(OperationInfo(
                        info.assetFile.get()!!,
                        "${info.label} MediaStore Uri",
                        mediaStoreUri,
                        info.target.get()!!
                ))
            }
        }

        return items
    }
}
