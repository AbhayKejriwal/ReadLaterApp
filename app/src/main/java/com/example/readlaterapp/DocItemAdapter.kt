package com.example.readlaterapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.readlaterapp.databinding.ItemViewBinding

class DocItemAdapter(private val onItemClick: (DocItem) -> Unit) :
    ListAdapter<DocItem, DocItemAdapter.DocItemViewHolder>(DocItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocItemViewHolder {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DocItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DocItemViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class DocItemViewHolder(private val binding: ItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    onItemClick(item)
                }
            }
        }

        fun bind(docItem: DocItem) {
            binding.docItem = docItem
            binding.executePendingBindings()
        }
    }

    class DocItemDiffCallback : DiffUtil.ItemCallback<DocItem>() {
        override fun areItemsTheSame(oldItem: DocItem, newItem: DocItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DocItem, newItem: DocItem): Boolean {
            return oldItem == newItem
        }
    }
}
