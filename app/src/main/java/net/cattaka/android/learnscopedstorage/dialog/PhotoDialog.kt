package net.cattaka.android.learnscopedstorage.dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import net.cattaka.android.learnscopedstorage.databinding.DialogPhotoBinding

class PhotoDialog : AppCompatDialogFragment() {
    lateinit var binding: DialogPhotoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogPhotoBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener { v ->
            dismiss()
        }
        return binding.root
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.requestFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.let { w ->
            w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
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