package com.example.readlaterapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var repository: DocItemRepository
    private lateinit var docViewModel: DocItemViewModel
    private lateinit var adapter: DocItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize repository and ViewModel
        repository = DocItemRepository(DocDatabase.getDatabase(this).docItemDao())
        docViewModel = ViewModelProvider(this).get(DocItemViewModel::class.java)

        // Initialize RecyclerView and Adapter
        adapter = DocItemAdapter { docItem ->
            // Handle item click
            handleItemClick(docItem)
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Handle incoming intents
        handleIntent(intent)

        // Observe LiveData from ViewModel
        docViewModel.allDocs.observe(this, Observer { docs ->
            docs?.let {
                adapter.submitList(it)
            }
        })

        // Example of observing archived documents
        // Replace with appropriate UI triggers (like button clicks) to switch filters
        docViewModel.getArchivedDocs().observe(this, Observer { archivedDocs ->
            // Update adapter with archivedDocs
            adapter.submitList(archivedDocs)
        })

        // Example of observing liked (starred) documents
        // Replace with appropriate UI triggers (like button clicks) to switch filters
        docViewModel.getStarredDocs().observe(this, Observer { starredDocs ->
            // Update adapter with starredDocs
            adapter.submitList(starredDocs)
        })
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.let { incomingIntent ->
            when {
                incomingIntent.action == Intent.ACTION_SEND && incomingIntent.type == "text/plain" -> {
                    val url = incomingIntent.getStringExtra(Intent.EXTRA_TEXT)
                    if (url != null) {
                        val docItem = DocItem(name = "Web Link", filepath = url)
                        insertDocItem(docItem)
                    } else {
                        Log.e("MainActivity", "No URL found in intent")
                    }
                }
                incomingIntent.action == Intent.ACTION_SEND && incomingIntent.type == "application/pdf" -> {
                    val uri = incomingIntent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                    if (uri != null) {
                        val pdfPath = getFilePathFromUri(uri)
                        val docItem = DocItem(name = "PDF Document", filepath = pdfPath)
                        insertDocItem(docItem)
                    } else {
                        Log.e("MainActivity", "No PDF URI found in intent")
                    }
                }
                else -> {
                    Log.e("MainActivity", "Unable to handle intent")
                }
            }
        }
    }

    private fun insertDocItem(docItem: DocItem) {
        lifecycleScope.launch {
            repository.insert(docItem)
        }
    }

    private fun getFilePathFromUri(uri: Uri): String {
        return uri.path ?: ""
    }

    private fun handleItemClick(docItem: DocItem) {
        if (docItem.filepath.startsWith("http:") || docItem.filepath.startsWith("https:")) {
            // Open web link
            val url = docItem.filepath
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        } else {
            // Open PDF document
            val uri = Uri.parse(docItem.filepath)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            startActivity(intent)
        }
    }
}
