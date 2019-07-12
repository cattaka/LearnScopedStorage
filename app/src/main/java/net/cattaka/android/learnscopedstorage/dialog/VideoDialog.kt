package net.cattaka.android.learnscopedstorage.dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import net.cattaka.android.learnscopedstorage.databinding.DialogVideoBinding
import net.cattaka.android.learnscopedstorage.util.concatMessages

class VideoDialog : AppCompatDialogFragment() {
    lateinit var binding: DialogVideoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogVideoBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener { v ->
            dismiss()
        }

        binding.buttonPrev.setOnClickListener { v -> binding.videoView.seekTo(0) }
        binding.buttonPlay.setOnClickListener { v -> binding.videoView.resume() }
        binding.buttonPause.setOnClickListener { v -> binding.videoView.pause() }
        binding.buttonNext.setOnClickListener { v -> binding.videoView.seekTo(binding.videoView.duration) }

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
        val uri = arguments?.getString(KEY_URI)?.let { Uri.parse(it) }
        try {
            if (uri != null) {
                binding.videoView.setVideoURI(uri)
                binding.videoView.start()
            } else {
                Toast.makeText(requireActivity(), "Uri is missing", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        } catch (e: Exception) {
            Toast.makeText(requireActivity(), e.concatMessages(), Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    override fun onStop() {
        super.onStop()
        binding.videoView.stopPlayback()
    }

    companion object {
        const val KEY_URI = "uri"
        fun newInstance(uri: String): VideoDialog {
            return VideoDialog().apply {
                arguments = bundleOf(
                        KEY_URI to uri
                )
            }
        }
    }
}
