package net.cattaka.android.learnscopedstorage.data

import androidx.databinding.Observable
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
        target.apply {
            addOnPropertyChangedCallback(object:Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    target.get()?.let { targetString.set(it.name)}
                }
            })
            set(targetValue)
        }
    }

    val targetString: ObservableField<String> = ObservableField("")
}
