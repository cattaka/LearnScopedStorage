package net.cattaka.android.learnscopedstorage.dialog

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import net.cattaka.android.learnscopedstorage.databinding.DialogPhotoBinding
import net.cattaka.android.learnscopedstorage.util.concatMessages

class PhotoDialog : AppCompatDialogFragment() {
    lateinit var binding: DialogPhotoBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
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
        val uri = arguments?.getString(KEY_URI)?.let {
            if (it.startsWith("/")) {
                "file://$it"
            } else {
                it
            }
        }
        Picasso.get().load(uri)
                .into(object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        Toast.makeText(
                                requireActivity(),
                                e?.concatMessages().toString(),
                                Toast.LENGTH_SHORT
                        ).show()
                        dismiss()
                    }

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        binding.imageView.setImageBitmap(bitmap)
                    }
                })
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