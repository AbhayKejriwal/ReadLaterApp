package com.example.readlaterapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.io.File

class ArchiveActivity : AppCompatActivity() {

    private lateinit var archiveAdapter: ArchiveAdapter
    private val docItemViewModel: DocItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        archiveAdapter = ArchiveAdapter(
            onItemClick = { docItem -> handleItemClick(docItem) },
            onArchiveClick = { docItem -> unarchiveItem(docItem) },
            onDeleteClick = { docItem -> deleteItem(docItem) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = archiveAdapter

        docItemViewModel.archivedDocs.observe(this, Observer { archivedDocs ->
            archiveAdapter.submitList(archivedDocs)
        })
    }

    private fun unarchiveItem(docItem: DocItem) {
        docItem.archived = false
        Log.i("ArchiveActivity","Removing from archive")
        lifecycleScope.launch {
            docItemViewModel.update(docItem)
        }
    }

    private fun deleteItem(docItem: DocItem) {
        Log.i("ArchiveActivity","deleting item")
        lifecycleScope.launch {
            docItemViewModel.delete(docItem)
        }
    }

    private fun handleItemClick(docItem: DocItem) {
        if (docItem.filepath.startsWith("http://") || docItem.filepath.startsWith("https://")) {
            // Open web link
            val url = docItem.filepath
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            startActivity(intent)
        } else {
            // Open PDF document
            val uri = Uri.parse(docItem.filepath)
            val file = File(uri.path ?: "")
            val pdfUri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(pdfUri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        }
    }
}
