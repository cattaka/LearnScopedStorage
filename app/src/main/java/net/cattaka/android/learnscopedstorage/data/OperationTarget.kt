package net.cattaka.android.learnscopedstorage.data

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
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
        when (info.targetValue) {
            IMAGE -> {
                PhotoDialog.newInstance(info.pathValue)
                    .show(activity.supportFragmentManager, "PHOTO_DIALOG")
            }
            AUDIO -> {
                AudioDialog.newInstance(info.pathValue)
                    .show(activity.supportFragmentManager, "AUDIO_DIALOG")
            }
            MOVIE -> {
                VideoDialog.newInstance(info.pathValue)
                    .show(activity.supportFragmentManager, "VIDEO_DIALOG")
            }
            DOWNLOAD,
            OTHER -> {
                TextDialog.newInstance(info.pathValue)
                    .show(activity.supportFragmentManager, "TEXT_DIALOG")
            }
        }
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

    @RequiresApi(Build.VERSION_CODES.Q)
    fun createViaMediaStore(activity: AppCompatActivity, info: OperationInfo) {
        wrap(activity) {
            val uri = Uri.parse(info.pathValue)
            val displayName = uri.lastPathSegment ?: uri.toString()
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, info.mimeValue)
                put(MediaStore.MediaColumns.IS_PENDING, 0)
            }

            val resolver = activity.contentResolver
            val collection = info.getContentUri(MediaStore.VOLUME_EXTERNAL)
            val item = resolver.insert(collection, values)
            if (item != null) {
                Toast.makeText(activity, "Succeed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun writeViaMediaStore(activity: AppCompatActivity, info: OperationInfo) {
        wrap(activity) {
            val uri = Uri.parse(info.pathValue)
            val displayName = uri.lastPathSegment ?: uri.toString()
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, info.mimeValue)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }

            val resolver = activity.contentResolver
            val collection = info.getContentUri(MediaStore.VOLUME_EXTERNAL)
            resolver.insert(collection, values)?.let { item ->
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

                values.clear()
                values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(item, values, null, null)
                Toast.makeText(activity, "Succeed", Toast.LENGTH_SHORT).show()
            } ?: Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun readViaMediaStore(activity: AppCompatActivity, info: OperationInfo) {
        wrap(activity) {
            val uri = Uri.parse(info.pathValue)
            val displayName = uri.lastPathSegment ?: uri.toString()

            val resolver = activity.contentResolver
            val collection = info.getContentUri(MediaStore.VOLUME_EXTERNAL)
            val collectionWithPending = MediaStore.setIncludePending(collection)
            val itemUri = resolver.query(
                collectionWithPending,
                null,
                bundleOf(MediaStore.MediaColumns.DISPLAY_NAME to displayName),
                null
            )?.use { c ->
                while (c.moveToNext()) {
                    val columnIndex = c.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)
                    return@use c.getString(columnIndex)
                }
                return@use null
            }?.let {
                collectionWithPending.buildUpon().appendPath(it).build()
            }

            if (itemUri != null) {
                when (info.targetValue) {
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
            } else {
                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun deleteViaMediaStore(activity: AppCompatActivity, info: OperationInfo) {
        wrap(activity) {
            val uri = Uri.parse(info.pathValue)
            val displayName = uri.lastPathSegment ?: uri.toString()

            val resolver = activity.contentResolver
            val collection = info.getContentUri(MediaStore.VOLUME_EXTERNAL)
            val collectionWithPending = MediaStore.setIncludePending(collection)
            val count = resolver.query(
                collectionWithPending,
                null,
                bundleOf(MediaStore.MediaColumns.DISPLAY_NAME to displayName),
                null
            )?.use { c ->
                while (c.moveToNext()) {
                    val columnIndex = c.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)
                    return@use c.getString(columnIndex)
                }
                return@use null
            }?.let {
                val itemUri = collectionWithPending.buildUpon().appendPath(it).build()
                resolver.delete(itemUri, null, null)
            } ?: 0

            val message = if (count == 0) "Failed" else "Succeed"
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
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
}
