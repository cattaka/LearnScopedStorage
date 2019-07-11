package net.cattaka.android.learnscopedstorage.data

import android.app.Activity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.cattaka.android.learnscopedstorage.dialog.PhotoDialog
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
    fun create(activity: AppCompatActivity, info: OperationInfo) {
        write(activity, info)
    }

    fun delete(activity: AppCompatActivity, info: OperationInfo) {
        wrap(activity) {
            File(info.pathValue).let {
                if (it.exists()) {
                    it.delete()
                } else {
                    Toast.makeText(activity, "File is not exist : ${it.path}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun read(activity: AppCompatActivity, info: OperationInfo) {
        when(info.targetValue) {
            IMAGE -> {
                PhotoDialog.newInstance(info.assetFileValue)
                        .show(activity.supportFragmentManager, "PHOTO_DIALOG")
            }
            AUDIO -> TODO()
            MOVIE -> TODO()
            DOWNLOAD -> TODO()
            OTHER -> TODO()
        }
    }

    @Throws(IOException::class)
    fun write(activity: AppCompatActivity, info: OperationInfo) {
        wrap(activity) {
            FileOutputStream(File(info.pathValue)).use { fout ->
                FileInputStream(File(info.assetFileValue)).use { fin ->
                    fin.copyTo(fout)
                }
            }
        }
    }

    private fun wrap(activity: AppCompatActivity, run: () -> Unit) {
        try {
            run()
        } catch (e: Exception) {
            val sb = StringBuilder("Error:")
            var e2: Throwable? = e
            while (e2 != null) {
                sb.append("\n")
                sb.append(e2.message)
                e2 = e2.cause
            }
            Toast.makeText(activity, sb.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}
