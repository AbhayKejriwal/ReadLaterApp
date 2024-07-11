package com.example.readlaterapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.FileProvider
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var repository: DocItemRepository
    private lateinit var docViewModel: DocItemViewModel
    private lateinit var adapter: DocItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize repository and ViewModel
        repository = DocItemRepository(DocDatabase.getDatabase(this).docItemDao())
        docViewModel = ViewModelProvider(this)[DocItemViewModel::class.java]

        // Initialize RecyclerView and Adapter
        adapter = DocItemAdapter(
            onItemClick = { docItem -> handleItemClick(docItem) },
            onArchiveClick = { docItem -> archiveItem(docItem) },
            onDeleteClick = { docItem -> deleteItem(docItem) }
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Observe LiveData from ViewModel
        docViewModel.allDocs.observe(this) { docs ->
            docs?.let {
                adapter.submitList(it)
            }
        }

        // Handle incoming intents
        handleIntent(intent)

        val archivebtn: ImageButton = findViewById(R.id.ArchiveButton)
        archivebtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, ArchiveActivity::class.java))
        }
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
                        val pdfPath = getFilePathFromUri(this, uri)
                        pdfPath?.let {
                            val docItem = DocItem(name = "PDF Document", filepath = it)
                            insertDocItem(docItem)
                        } ?: Log.e("MainActivity", "Unable to get PDF path from URI")
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

    private fun archiveItem(docItem: DocItem) {
        docItem.archived = true
        lifecycleScope.launch {
            repository.update(docItem)
        }
    }

    private fun deleteItem(docItem: DocItem) {
        lifecycleScope.launch {
            repository.delete(docItem)
        }
    }

    private fun getFilePathFromUri(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            if (idx != -1) {
                filePath = cursor.getString(idx)
            }
            cursor.close()
        }
        if (filePath == null) {
            filePath = uri.path
        }
        return filePath
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
            val file = File(uri.path)
            val pdfUri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(pdfUri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        }
    }
}
