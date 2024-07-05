package com.example.readlaterapp// com.example.readlaterapp.DocItemAdapter.kt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.readlaterapp.databinding.ItemViewBinding

class DocItemAdapter(private val onItemClick: (DocItem) -> Unit) :
    ListAdapter<DocItem, DocItemAdapter.DocItemViewHolder>(ItemAdapter.DocItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocItemViewHolder {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DocItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DocItemViewHolder, position: Int) {
        val docItem = getItem(position)
        holder.bind(docItem)
    }

    inner class DocItemViewHolder(private val binding: ItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(docItem: DocItem) {
            binding.docItem = docItem
            binding.executePendingBindings()

            itemView.setOnClickListener {
                onItemClick.invoke(docItem)
            }
        }
    }
}
