package net.cattaka.android.learnscopedstorage.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import net.cattaka.android.learnscopedstorage.databinding.DialogPhotoBinding

class PhotoDialog : AppCompatDialogFragment() {
    lateinit var binding: DialogPhotoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.uri = arguments?.getString(KEY_URI)
    }

    companion object {
        const val KEY_URI = "uri"
        fun newInstance(uri: String): PhotoDialog {
            return PhotoDialog().apply {
                arguments = bundleOf(
                        KEY_URI to uri
                )
            }
        }
    }
}