package net.cattaka.android.learnscopedstorage.data

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.cattaka.android.learnscopedstorage.dialog.AudioDialog
import net.cattaka.android.learnscopedstorage.dialog.PhotoDialog
import net.cattaka.android.learnscopedstorage.dialog.TextDialog
import net.cattaka.android.learnscopedstorage.dialog.VideoDialog
import net.cattaka.android.learnscopedstorage.util.concatMessages
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

enum class OperationTarget {
    IMAGE,
    AUDIO,
    MOVIE,
    DOWNLOAD,
    OTHER
    ;

    @Throws(IOException::class)
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

    @Throws(IOException::class)
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

    private fun wrap(activity: AppCompatActivity, run: () -> Unit) {
        try {
            run()
        } catch (e: Exception) {
            Toast.makeText(activity, e.concatMessages(), Toast.LENGTH_SHORT).show()
        }
    }
}
