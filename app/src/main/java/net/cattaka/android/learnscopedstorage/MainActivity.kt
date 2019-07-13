package net.cattaka.android.learnscopedstorage

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.arch.core.util.Function
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.cattaka.android.learnscopedstorage.adapter.OperationInfoAdapter
import net.cattaka.android.learnscopedstorage.data.OperationDestination
import net.cattaka.android.learnscopedstorage.data.OperationInfo
import net.cattaka.android.learnscopedstorage.data.OperationTarget
import net.cattaka.android.learnscopedstorage.data.OperationType
import net.cattaka.android.learnscopedstorage.databinding.ActivityMainBinding
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File

@RuntimePermissions
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var adapter: OperationInfoAdapter

    val operationInfoAdapterListener = object : OperationInfoAdapter.OperationInfoAdapterListener {
        override fun onCLickCreate(holder: OperationInfoAdapter.ViewHolder, info: OperationInfo) {
            doWithCheckPermission(OperationType.CREATE, info)
        }

        override fun onCLickDelete(holder: OperationInfoAdapter.ViewHolder, info: OperationInfo) {
            doWithCheckPermission(OperationType.DELETE, info)
        }

        override fun onCLickRead(holder: OperationInfoAdapter.ViewHolder, info: OperationInfo) {
            doWithCheckPermission(OperationType.READ, info)
        }

        override fun onCLickWrite(holder: OperationInfoAdapter.ViewHolder, info: OperationInfo) {
            doWithCheckPermission(OperationType.WRITE, info)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        adapter = OperationInfoAdapter(prepareItems().toMutableList()).apply {
            this.listener = operationInfoAdapterListener
        }

        binding.recyclerView.apply {
            this.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            this.adapter = this@MainActivity.adapter
        }
    }

    private fun prepareItems(): List<OperationInfo> {
        val items = mutableListOf<OperationInfo>()
        val photoDirect = OperationInfo(
                assets.openFd("photo.png"),
                "Photo",
                "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path}/photo.png",
                OperationTarget.IMAGE,
                OperationDestination.EXTERNAL
        )
        val audioDirect = OperationInfo(
                assets.openFd("audio.ogg"),
                "Audio",
                "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).path}/audio.ogg",
                OperationTarget.AUDIO,
                OperationDestination.EXTERNAL
        )
        val movieDirect = OperationInfo(
                assets.openFd("movie.webm"),
                "Movie",
                "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).path}/movie.webm",
                OperationTarget.MOVIE,
                OperationDestination.EXTERNAL
        )
        val downloadDirect = OperationInfo(
                assets.openFd("text.txt"),
                "Download",
                "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path}/text.txt",
                OperationTarget.DOWNLOAD,
                OperationDestination.EXTERNAL
        )
        items.add(photoDirect)
        items.add(audioDirect)
        items.add(movieDirect)
        items.add(downloadDirect)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (info in arrayOf(photoDirect, audioDirect, movieDirect, downloadDirect)) {
                val mediaStoreUri = MediaStore.setRequireOriginal(Uri.fromFile(File(info.path.get()!!))).toString()
                items.add(OperationInfo(
                        info.assetFile.get()!!,
                        "${info.label} MediaStore Uri",
                        mediaStoreUri,
                        info.target.get()!!,
                        OperationDestination.MEDIA_STORE
                ))
            }
        }

        return items
    }

    private fun doWithCheckPermission(type: OperationType, info: OperationInfo) {
        when (info.destinationValue) {
            OperationDestination.MEDIA_STORE -> {
                when (type) {
                    OperationType.CREATE -> info.targetValue.createClassic(this, info)
                    OperationType.DELETE -> info.targetValue.deleteClassic(this, info)
                    OperationType.READ -> info.targetValue.readClassic(this, info)
                    OperationType.WRITE -> info.targetValue.writeClassic(this, info)
                }
            }
            OperationDestination.INTERNAL -> {
                when (type) {
                    OperationType.CREATE -> info.targetValue.createClassic(this, info)
                    OperationType.DELETE -> info.targetValue.deleteClassic(this, info)
                    OperationType.READ -> info.targetValue.readClassic(this, info)
                    OperationType.WRITE -> info.targetValue.writeClassic(this, info)
                }
            }
            OperationDestination.EXTERNAL -> {
                val f = Function<MainActivity, Unit> { activity ->
                    when (type) {
                        OperationType.CREATE -> info.targetValue.createClassic(activity, info)
                        OperationType.DELETE -> info.targetValue.deleteClassic(activity, info)
                        OperationType.READ -> info.targetValue.readClassic(activity, info)
                        OperationType.WRITE -> info.targetValue.writeClassic(activity, info)
                    }
                }
                when (type) {
                    OperationType.READ -> readActionWithPermissionCheck(f)
                    OperationType.CREATE,
                    OperationType.DELETE,
                    OperationType.WRITE -> writeActionWithPermissionCheck(f)
                }

            }
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun readAction(f: Function<MainActivity, Unit>) {
        f.apply(this)
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun writeAction(f: Function<MainActivity, Unit>) {
        f.apply(this)
    }
}
