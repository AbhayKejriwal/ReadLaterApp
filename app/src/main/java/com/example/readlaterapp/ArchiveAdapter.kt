package com.example.readlaterapp

import  android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.readlaterapp.databinding.ItemViewBinding

class ArchiveAdapter(private val onItemClick: (DocItem) -> Unit) :
    RecyclerView.Adapter<ArchiveAdapter.ArchiveViewHolder>() {

    private var archivedDocs: List<DocItem> = emptyList()

    fun submitList(docs: List<DocItem>) {
        archivedDocs = docs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchiveViewHolder {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArchiveViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArchiveViewHolder, position: Int) {
        val currentItem = archivedDocs[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return archivedDocs.size
    }

    inner class ArchiveViewHolder(private val binding: ItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = archivedDocs[position]
                    onItemClick(item)
                }
            }
        }

        fun bind(docItem: DocItem) {
            binding.docItem = docItem
            binding.executePendingBindings()
        }
    }
}
