package net.cattaka.android.learnscopedstorage.data

import android.app.Activity
import android.widget.Toast
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
    fun create(activity: Activity, info: OperationInfo) {
        write(activity, info)
    }

    fun delete(activity: Activity, info: OperationInfo) {
        File(info.pathValue).takeIf { it.exists() }?.let { it.delete() }
    }

    fun read(activity: Activity, info: OperationInfo) {
        Toast.makeText(activity, "Not implemented yet.", Toast.LENGTH_SHORT).show()
    }

    @Throws(IOException::class)
    fun write(activity: Activity, info: OperationInfo) {
        FileOutputStream(File(info.pathValue)).use { fout ->
            FileInputStream(File(info.assetFileValue)).use { fin ->
                fin.copyTo(fout)
            }
        }
    }

}
