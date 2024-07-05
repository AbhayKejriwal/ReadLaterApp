package com.example.readlaterapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(private val onItemClick: (DocItem) -> Unit) :
    ListAdapter<DocItem, ItemAdapter.ItemViewHolder>(DocItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val docItem = getItem(position)
        holder.bind(docItem)
        holder.itemView.setOnClickListener {
            onItemClick(docItem)
        }
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val pathOrUrlTextView: TextView = itemView.findViewById(R.id.pathOrUrlTextView)

        fun bind(docItem: DocItem) {
            titleTextView.text = docItem.name
            pathOrUrlTextView.text = docItem.filepath
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
