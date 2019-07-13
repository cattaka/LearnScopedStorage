package net.cattaka.android.learnscopedstorage.data

import android.content.ContentUris
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import net.cattaka.android.learnscopedstorage.dialog.AudioDialog
import net.cattaka.android.learnscopedstorage.dialog.PhotoDialog
import net.cattaka.android.learnscopedstorage.dialog.TextDialog
import net.cattaka.android.learnscopedstorage.dialog.VideoDialog
import net.cattaka.android.learnscopedstorage.util.concatMessages
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

enum class OperationTarget() {
    IMAGE,
    AUDIO,
    MOVIE,
    DOWNLOAD,
    OTHER
    ;

    fun createClassic(activity: AppCompatActivity, info: OperationInfo) {
        wrap(activity) {
            FileOutputStream(File(info.pathValue)).use {}
        }
    }

    fun deleteClassic(activity: AppCompatActivity, info: OperationInfo) {
        wrap(activity) {
            File(info.pathValue).let {
                if (it.exists()) {
                    it.delete()
                } else {
                    Toast.makeText(activity, "File is not exist : ${it.path}", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        }
    }

    fun readClassic(activity: AppCompatActivity, info: OperationInfo) {
        openByDialog(activity, info.targetValue, Uri.parse(info.pathValue))
    }

    fun writeClassic(activity: AppCompatActivity, info: OperationInfo) {
        wrap(activity) {
            FileOutputStream(File(info.pathValue)).use { fout ->
                FileInputStream(info.assetFileValue.fileDescriptor).use { fin ->
                    fin.skip(info.assetFileValue.startOffset)
                    val buffer = ByteArray(info.assetFileValue.length.toInt())
                    fin.read(buffer)
                    fout.write(buffer)
                }
                fout.flush()
            }
        }
    }

    fun createViaMediaStore(activity: AppCompatActivity, info: OperationInfo) {
        wrap(activity) {
            val uri = Uri.parse(info.pathValue)
            val displayName = uri.lastPathSegment ?: uri.toString()
            val values = ContentValues().apply {
                //put(MediaStore.MediaColumns.RELATIVE_PATH, ""/*TODO*/)
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, info.mimeValue)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.IS_PENDING, 0)
                }
            }

            val resolver = activity.contentResolver
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                info.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                info.externalContentUri
            }
            val item = resolver.insert(collection, values)
            if (item != null) {
                Toast.makeText(activity, "Succeed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun writeViaMediaStore(activity: AppCompatActivity, info: OperationInfo) {
        wrap(activity) {
            val resolver = activity.contentResolver
            val item = Uri.parse(info.pathValue).let {
                val displayName = it.lastPathSegment ?: it.toString()
                val values = ContentValues().apply {
                    //put(MediaStore.MediaColumns.RELATIVE_PATH, ""/*TODO*/)
                    put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                    put(MediaStore.MediaColumns.MIME_TYPE, info.mimeValue)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.MediaColumns.IS_PENDING, 1)
                    }
                }
                val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    info.getContentUri(MediaStore.VOLUME_EXTERNAL)
                } else {
                    info.externalContentUri
                }
                resolver.insert(collection, values)
            }
            item?.let { item ->
                resolver.openFileDescriptor(item, "w", null)?.let { pfd ->
                    FileOutputStream(pfd.fileDescriptor).use { fout ->
                        FileInputStream(info.assetFileValue.fileDescriptor).use { fin ->
                            fin.skip(info.assetFileValue.startOffset)
                            val buffer = ByteArray(info.assetFileValue.length.toInt())
                            fin.read(buffer)
                            fout.write(buffer)
                        }
                        fout.flush()
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values = ContentValues().apply {
                        put(MediaStore.MediaColumns.IS_PENDING, 0)
                    }
                    resolver.update(item, values, null, null)
                }
                Toast.makeText(activity, "Succeed", Toast.LENGTH_SHORT).show()
            } ?: Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun readViaMediaStore(activity: AppCompatActivity, info: OperationInfo) {
        wrap(activity) {
            val uri = Uri.parse(info.pathValue)
            val itemUri = findUri(activity, info)
            if (itemUri != null) {
                openByDialog(activity, info.targetValue, itemUri)
            } else {
                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteViaMediaStore(activity: AppCompatActivity, info: OperationInfo) {
        wrap(activity) {
            val uri = Uri.parse(info.pathValue)
            val displayName = uri.lastPathSegment ?: uri.toString()

            val resolver = activity.contentResolver
            val count = findUri(activity, info)?.let {
                resolver.delete(it, null, null)
            } ?: 0

            val message = if (count == 0) "Failed" else "Succeed"
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun openViaMediaStore(activity: AppCompatActivity, itemUri: Uri) {
            wrap(activity) {
                val prefix2Targets = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    mapOf(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() to OperationTarget.IMAGE,
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString() to OperationTarget.AUDIO,
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString() to OperationTarget.MOVIE,
                            MediaStore.Downloads.EXTERNAL_CONTENT_URI.toString() to OperationTarget.DOWNLOAD
                    )
                } else {
                    mapOf(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() to OperationTarget.IMAGE,
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString() to OperationTarget.AUDIO,
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString() to OperationTarget.MOVIE
                    )
                }

                val s = itemUri.toString()
                val target = prefix2Targets.entries.find { s.startsWith(it.key, false) }?.value

                if (target != null) {
                    openByDialog(activity, target, itemUri)
                } else {
                    Toast.makeText(activity, "Could not detect mime for $itemUri", Toast.LENGTH_SHORT).show()
                    return@wrap
                }
            }
        }

        private fun openByDialog(activity: AppCompatActivity, target: OperationTarget, itemUri: Uri) {
            when (target) {
                IMAGE -> {
                    PhotoDialog.newInstance(itemUri.toString())
                            .show(activity.supportFragmentManager, "PHOTO_DIALOG")
                }
                AUDIO -> {
                    AudioDialog.newInstance(itemUri.toString())
                            .show(activity.supportFragmentManager, "AUDIO_DIALOG")
                }
                MOVIE -> {
                    VideoDialog.newInstance(itemUri.toString())
                            .show(activity.supportFragmentManager, "VIDEO_DIALOG")
                }
                DOWNLOAD,
                OTHER -> {
                    TextDialog.newInstance(itemUri.toString())
                            .show(activity.supportFragmentManager, "TEXT_DIALOG")
                }
            }
        }

        private fun wrap(activity: AppCompatActivity, run: () -> Unit) {
            try {
                run()
            } catch (e: Exception) {
                Toast.makeText(activity, e.concatMessages(), Toast.LENGTH_SHORT).show()
            }
        }

        private fun <T> wrapReturn(activity: AppCompatActivity, defaultValue: T, run: () -> T): T {
            return try {
                run()
            } catch (e: Exception) {
                Toast.makeText(activity, e.concatMessages(), Toast.LENGTH_SHORT).show()
                defaultValue
            }
        }

        fun findUri(activity: AppCompatActivity, info: OperationInfo, includePending: Boolean = true): Uri? {
            val uri = Uri.parse(info.pathValue)
            val displayName = uri.lastPathSegment ?: uri.toString()

            val resolver = activity.contentResolver
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                resolver.query(
                        MediaStore.setIncludePending(info.getContentUri(MediaStore.VOLUME_EXTERNAL)),
                        null,
                        bundleOf(MediaStore.MediaColumns.DISPLAY_NAME to displayName),
                        null
                )
            } else {
                resolver.query(
                        info.externalContentUri,
                        null,
                        "${MediaStore.MediaColumns.DISPLAY_NAME}=?",
                        arrayOf(displayName),
                        null
                )
            }?.use { c ->
                while (c.moveToNext()) {
                    val columnIndex = c.getColumnIndexOrThrow(BaseColumns._ID)
                    return@use c.getLong(columnIndex)
                }
                return@use null
            }?.let {
                ContentUris.withAppendedId(info.externalContentUri, it)
            }
        }
    }
}
