package net.cattaka.android.learnscopedstorage.data

import androidx.databinding.ObservableField

class OperationInfo(
        assetsFileValue: String,
        labelValue: String,
        pathValue: String,
        targetValue: OperationTarget
) {
    val assetFile: ObservableField<String> = ObservableField()
    val label: ObservableField<String> = ObservableField()
    val path: ObservableField<String> = ObservableField()
    val target: ObservableField<OperationTarget> = ObservableField()

    init {
        assetFile.set(assetsFileValue)
        label.set(labelValue)
        path.set(pathValue)
        target.set(targetValue)
    }
}
