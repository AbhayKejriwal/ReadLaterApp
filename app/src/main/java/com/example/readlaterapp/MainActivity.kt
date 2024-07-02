package com.example.readlaterapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.parse

class MainActivity : AppCompatActivity() {

    private lateinit var repository: DocItemRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
                        Log.d("MainActivity", "Handling web link: $url")
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
                        Log.d("MainActivity", "Handling PDF: $pdfPath")
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
            Log.d("MainActivity", "Inserted DocItem: ${docItem.filepath}")
        }
    }

    private fun getFilePathFromUri(uri: Uri): String {
        return uri.path ?: ""
    }

    private fun ItemClick(docItem: DocItem){
        if(docItem.filepath.startsWith("http:") or docItem.filepath.startsWith("https:")){
            val url = docItem.filepath
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse(url))
            startActivity(i)
        }
        else{
            val uri = Uri.parse(docItem.filepath)
            val i = Intent(Intent.ACTION_VIEW)
            i.setDataAndType(uri, "application/pdf")
            startActivity(i)
        }
    }
}
