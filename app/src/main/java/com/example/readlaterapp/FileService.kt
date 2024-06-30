package com.example.readlaterapp

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.provider.OpenableColumns
import android.util.Log
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class FileProcessingService : Service() {

    private lateinit var repository: DocItemRepository
    private val TAG = "FileProcessingService"

    override fun onCreate() {
        super.onCreate()
        val database = Room.databaseBuilder(applicationContext, DocDatabase::class.java, "Doc_Database").build()
        repository = DocItemRepository(database.docItemDao())
        Log.i(TAG, "Service created and database initialized")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            Log.i(TAG, "Service started with intent: ${it.action}")
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    handleIncomingIntent(applicationContext, it)
                } catch (e: Exception) {
                    Log.e(TAG, "Error handling intent: ${e.message}", e)
                } finally {
                    stopSelf()
                    Log.i(TAG, "Service stopped")
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private suspend fun handleIncomingIntent(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SEND -> {
                Log.d(TAG, "Handling single send intent")
                handleSendIntent(context, intent)
            }
            Intent.ACTION_SEND_MULTIPLE -> {
                Log.d(TAG, "Handling multiple send intent")
                handleSendMultipleIntent(context, intent)
            }
            else -> {
                Log.w(TAG, "Unhandled intent action: ${intent.action}")
            }
        }
    }

    private suspend fun handleSendIntent(context: Context, intent: Intent) {
        intent.type?.let { type ->
            if (type.startsWith("text/")) {
                intent.getStringExtra(Intent.EXTRA_TEXT)?.let { url ->
                    Log.d(TAG, "Received URL: $url")
                    saveLinkToDatabase(url)
                } ?: run {
                    Log.w(TAG, "No URL found in intent")
                }
            } else {
                intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)?.let { uri ->
                    Log.d(TAG, "Received file URI: $uri")
                    savePdfToDatabase(context, uri)
                } ?: run {
                    Log.w(TAG, "No file URI found in intent")
                }
            }
        }
    }

    private suspend fun handleSendMultipleIntent(context: Context, intent: Intent) {
        intent.type?.let { type ->
            if (type.startsWith("text/")) {
                intent.getStringArrayListExtra(Intent.EXTRA_TEXT)?.let { urls ->
                    Log.d(TAG, "Received multiple URLs: $urls")
                    for (url in urls) {
                        saveLinkToDatabase(url)
                    }
                } ?: run {
                    Log.w(TAG, "No URLs found in intent")
                }
            } else {
                intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)?.let { uris ->
                    Log.d(TAG, "Received multiple file URIs: $uris")
                    for (uri in uris) {
                        savePdfToDatabase(context, uri)
                    }
                } ?: run {
                    Log.w(TAG, "No file URIs found in intent")
                }
            }
        }
    }

    private suspend fun savePdfToDatabase(context: Context, uri: Uri) {
        try {
            val filePath = savePdfToInternalStorage(context, uri)
            val docItem = DocItem(name = getFileName(context, uri), filepath = filePath)
            repository.insert(docItem)
            Log.i(TAG, "Saved PDF to database with path: $filePath")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving PDF to database: ${e.message}", e)
        }
    }

    private suspend fun saveLinkToDatabase(url: String) {
        try {
            val docItem = DocItem(name = url, filepath = url)
            repository.insert(docItem)
            Log.i(TAG, "Saved URL to database: $url")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving URL to database: ${e.message}", e)
        }
    }

    private suspend fun savePdfToInternalStorage(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, getFileName(context, uri))
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()

            Log.d(TAG, "Saved PDF to internal storage with path: ${file.absolutePath}")
            file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Error saving PDF to internal storage: ${e.message}", e)
            throw e
        }
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        Log.d(TAG, "Extracted file name: $result")
        return result!!
    }
}
