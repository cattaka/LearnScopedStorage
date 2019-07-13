package net.cattaka.android.learnscopedstorage

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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
import net.cattaka.android.learnscopedstorage.dialog.InputUriDialog
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File


@RuntimePermissions
class MainActivity : AppCompatActivity(), InputUriDialog.InputUriDialogListener {
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

        override fun onCLickCopyUri(holder: OperationInfoAdapter.ViewHolder, info: OperationInfo) {
            val uri = OperationTarget.findUri(this@MainActivity, info)
            if (uri != null) {
                val cm = (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                cm.setPrimaryClip(
                    ClipData.newPlainText(
                        "Uri for ContentResolver",
                        uri.toString()
                    )
                )
                Toast.makeText(this@MainActivity, "Copied : $uri", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        adapter = OperationInfoAdapter(prepareItems().toMutableList()).apply {
            this.listener = operationInfoAdapterListener
        }

        binding.recyclerView.apply {
            this.layoutManager =
                LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            this.adapter = this@MainActivity.adapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_open_uri -> {
                InputUriDialog.newInstance().show(supportFragmentManager, "INPUT_URI_DIALOG")
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun prepareItems(): List<OperationInfo> {
        val items = mutableListOf<OperationInfo>()
        val photoDirect = OperationInfo(
            "photo.png",
            "Photo",
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path}/photo.png",
            "image/png",
            OperationTarget.IMAGE,
            OperationDestination.EXTERNAL,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ) { volumeName -> MediaStore.Images.Media.getContentUri(volumeName) }
        val audioDirect = OperationInfo(
            "audio.ogg",
            "Audio",
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).path}/audio.ogg",
            "audio/ogg",
            OperationTarget.AUDIO,
            OperationDestination.EXTERNAL,
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        ) { volumeName -> MediaStore.Audio.Media.getContentUri(volumeName) }
        val movieDirect = OperationInfo(
            "movie.webm",
            "Movie",
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).path}/movie.webm",
            "video/webm",
            OperationTarget.MOVIE,
            OperationDestination.EXTERNAL,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        ) { volumeName -> MediaStore.Video.Media.getContentUri(volumeName) }
        val downloadDirect = OperationInfo(
            "text.txt",
            "Download",
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path}/text.txt",
            "plain/text",
            OperationTarget.DOWNLOAD,
            OperationDestination.EXTERNAL,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Downloads.EXTERNAL_CONTENT_URI
            } else {
                // Dummy Uri
                Uri.fromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
            }
        ) { volumeName ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Downloads.getContentUri(volumeName)
            } else {
                throw IllegalArgumentException("Error case")
            }
        }
        items.add(photoDirect)
        items.add(audioDirect)
        items.add(movieDirect)
        items.add(downloadDirect)

        for (info in arrayOf(photoDirect, audioDirect, movieDirect)) {
            val mediaStoreUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.setRequireOriginal(Uri.fromFile(File(info.path.get()!!))).toString()
            } else {
                Uri.fromFile(File(info.path.get()!!)).toString()
            }
            items.add(
                OperationInfo(
                    info.assetFileValue,
                    "${info.label} MediaStore Uri",
                    mediaStoreUri,
                    info.mimeValue,
                    info.targetValue,
                    OperationDestination.MEDIA_STORE,
                    info.externalContentUri,
                    info.getContentUri
                )
            )
        }

        return items
    }

    private fun doWithCheckPermission(type: OperationType, info: OperationInfo) {
        when (info.destinationValue) {
            OperationDestination.MEDIA_STORE -> {
                when (type) {
                    OperationType.CREATE -> info.targetValue.createViaMediaStore(this, info)
                    OperationType.DELETE -> info.targetValue.deleteViaMediaStore(this, info)
                    OperationType.READ -> info.targetValue.readViaMediaStore(this, info)
                    OperationType.WRITE -> info.targetValue.writeViaMediaStore(this, info)
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

    override fun onClickInputUriDialogOk(uri: String) {
        OperationTarget.openViaMediaStore(this, Uri.parse(uri))
    }
}
