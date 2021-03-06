package net.cattaka.android.learnscopedstorage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import net.cattaka.android.learnscopedstorage.data.OperationDestination
import net.cattaka.android.learnscopedstorage.data.OperationInfo
import net.cattaka.android.learnscopedstorage.data.OperationTarget
import net.cattaka.android.learnscopedstorage.databinding.ItemOperationTargetBinding

class OperationInfoAdapter(
        val items: MutableList<OperationInfo>
) : RecyclerView.Adapter<OperationInfoAdapter.ViewHolder>() {
    var listener: OperationInfoAdapterListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemOperationTargetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )).apply {
            binding.buttonCreate.setOnClickListener { v -> binding.info?.let { listener?.onCLickCreate(this, it) } }
            binding.buttonDelete.setOnClickListener { v -> binding.info?.let { listener?.onCLickDelete(this, it) } }
            binding.buttonRead.setOnClickListener { v -> binding.info?.let { listener?.onCLickRead(this, it) } }
            binding.buttonWrite.setOnClickListener { v -> binding.info?.let { listener?.onCLickWrite(this, it) } }
            binding.buttonCopyUri.setOnClickListener { v -> binding.info?.let { listener?.onCLickCopyUri(this, it) } }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.info = items.elementAtOrNull(position)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(val binding: ItemOperationTargetBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        @JvmStatic
        @BindingAdapter("text")
        fun TextView.text(target: OperationTarget?) {
            text = target?.name ?: ""
        }

        @JvmStatic
        @BindingAdapter("text")
        fun TextView.text(destination: OperationDestination?) {
            text = destination?.name ?: ""
        }
    }

    interface OperationInfoAdapterListener {
        fun onCLickCreate(holder: ViewHolder, info: OperationInfo)
        fun onCLickDelete(holder: ViewHolder, info: OperationInfo)
        fun onCLickRead(holder: ViewHolder, info: OperationInfo)
        fun onCLickWrite(holder: ViewHolder, info: OperationInfo)
        fun onCLickCopyUri(holder: ViewHolder, info: OperationInfo)
    }
}
