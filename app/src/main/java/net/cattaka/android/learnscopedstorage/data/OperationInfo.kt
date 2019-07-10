package net.cattaka.android.learnscopedstorage.data

import androidx.databinding.ObservableField

class OperationInfo(
        labelValue: String,
        pathValue: String,
        targetValue: OperationTarget
) {
    val label: ObservableField<String> = ObservableField()
    val path: ObservableField<String> = ObservableField()
    val target: ObservableField<OperationTarget> = ObservableField()

    init {
        label.set(labelValue)
        path.set(pathValue)
        target.set(targetValue)
    }
}
