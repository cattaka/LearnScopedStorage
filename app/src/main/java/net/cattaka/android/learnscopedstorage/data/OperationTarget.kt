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

    fun read(activity: Activity, info: OperationInfo) {
        wrap(activity) {
            Toast.makeText(activity, "Not implemented yet.", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    fun write(activity: Activity, info: OperationInfo) {
        wrap(activity) {
            FileOutputStream(File(info.pathValue)).use { fout ->
                FileInputStream(File(info.assetFileValue)).use { fin ->
                    fin.copyTo(fout)
                }
            }
        }
    }

    private fun wrap(activity: Activity, run: () -> Unit) {
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
