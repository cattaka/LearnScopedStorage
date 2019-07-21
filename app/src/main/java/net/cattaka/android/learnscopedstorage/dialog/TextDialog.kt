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
import net.cattaka.android.learnscopedstorage.databinding.DialogTextBinding
import net.cattaka.android.learnscopedstorage.util.concatMessages
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader

class TextDialog : AppCompatDialogFragment() {
    lateinit var binding: DialogTextBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogTextBinding.inflate(inflater, container, false)
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
            w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onStart() {
        super.onStart()
        arguments?.getString(KEY_URI)?.let { path ->
            try {
                binding.text = readRawViaMediaStore(path)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.concatMessages(), Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }

    private fun readRawViaMediaStore(path: String): String {
        return if (path.startsWith("content://", false)) {
            val resolver = requireContext().contentResolver
            resolver.openInputStream(Uri.parse(path))?.use {
                InputStreamReader(it).readLines().joinToString("\n")
            } ?: ""
        } else {
            FileReader(File(path)).use { it.readLines().joinToString("\n") }
        }
    }

    companion object {
        const val KEY_URI = "uri"
        fun newInstance(uri: String): TextDialog {
            return TextDialog().apply {
                arguments = bundleOf(
                    KEY_URI to uri
                )
            }
        }
    }
}