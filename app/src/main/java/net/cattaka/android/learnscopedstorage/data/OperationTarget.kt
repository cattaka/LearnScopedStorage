package net.cattaka.android.learnscopedstorage.data

import android.app.Activity

enum class OperationTarget {
    IMAGE,
    AUDIO,
    MOVIE,
    DOWNLOAD,
    OTHER
    ;

    fun create(activity: Activity, info: OperationInfo) {

    }

    fun delete(activity: Activity, info: OperationInfo) {

    }

    fun read(activity: Activity, info: OperationInfo) {

    }

    fun write(activity: Activity, info: OperationInfo) {

    }

}
