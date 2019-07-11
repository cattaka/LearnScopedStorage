package net.cattaka.android.learnscopedstorage.util

import android.widget.Toast

fun Throwable.concatMessages() : String {
    val sb = StringBuilder("Error:")
    var e2: Throwable? = this
    while (e2 != null) {
        sb.append("\n")
        sb.append(e2.message)
        e2 = e2.cause
    }
    return sb.toString()
}
