package net.cattaka.android.learnscopedstorage.dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatDialogFragment
import net.cattaka.android.learnscopedstorage.databinding.DialogInputUriBinding

class InputUriDialog : AppCompatDialogFragment() {
    lateinit var binding: DialogInputUriBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DialogInputUriBinding.inflate(inflater, container, false)
        binding.buttonCancel.setOnClickListener { v ->
            dismiss()
        }
        binding.buttonOk.setOnClickListener { v ->
            val result = binding.editText.text.toString()
            (parentFragment as? InputUriDialogListener
                    ?: activity as? InputUriDialogListener)?.onClickInputUriDialogOk(result)
            dismiss()
        }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCancelable(false)
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
    }

    interface InputUriDialogListener {
        fun onClickInputUriDialogOk(uri: String)
    }

    companion object {
        fun newInstance(): InputUriDialog {
            return InputUriDialog()
        }
    }
}