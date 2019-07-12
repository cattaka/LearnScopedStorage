package net.cattaka.android.learnscopedstorage.data

import android.content.res.AssetFileDescriptor
import androidx.databinding.ObservableField

class OperationInfo(
        _assetsFile: AssetFileDescriptor,
        _label: String,
        _path: String,
        _target: OperationTarget,
        _destination: OperationDestination
) {
    val assetFile: ObservableField<AssetFileDescriptor> = ObservableField()
    val label: ObservableField<String> = ObservableField()
    val path: ObservableField<String> = ObservableField()
    val target: ObservableField<OperationTarget> = ObservableField()
    val destination: ObservableField<OperationDestination> = ObservableField()

    val assetFileValue: AssetFileDescriptor
        get() = assetFile.get()!!
    val labelValue: String
        get() = label.get() ?: ""
    val pathValue: String
        get() = path.get() ?: ""
    val targetValue: OperationTarget
        get() = target.get() ?: OperationTarget.OTHER
    val destinationValue: OperationDestination
        get() = destination.get() ?: OperationDestination.EXTERNAL

    init {
        assetFile.set(_assetsFile)
        label.set(_label)
        path.set(_path)
        target.set(_target)
        destination.set(_destination)
    }
}
