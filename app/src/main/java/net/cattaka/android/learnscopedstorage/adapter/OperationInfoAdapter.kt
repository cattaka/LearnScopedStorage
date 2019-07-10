package net.cattaka.android.learnscopedstorage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.cattaka.android.learnscopedstorage.data.OperationInfo
import net.cattaka.android.learnscopedstorage.databinding.ItemOperationTargetBinding

class OperationInfoAdapter(
        val items: MutableList<OperationInfo>
) : RecyclerView.Adapter<OperationInfoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemOperationTargetBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.info = items.elementAtOrNull(position)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(val binding: ItemOperationTargetBinding) : RecyclerView.ViewHolder(binding.root)
}
