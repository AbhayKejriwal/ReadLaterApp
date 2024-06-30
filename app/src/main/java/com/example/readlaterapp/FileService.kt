package com.example.readlaterapp

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.provider.OpenableColumns
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

    override fun onCreate() {
        super.onCreate()
        val database = Room.databaseBuilder(applicationContext, DocDatabase::class.java, "Doc_Database").build()
        repository = DocItemRepository(database.docItemDao())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            CoroutineScope(Dispatchers.IO).launch {
                handleIncomingIntent(applicationContext, it)
                stopSelf()
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
                handleSendIntent(context, intent)
            }
            Intent.ACTION_SEND_MULTIPLE -> {
                handleSendMultipleIntent(context, intent)
            }
        }
    }

    private suspend fun handleSendIntent(context: Context, intent: Intent) {
        intent.type?.let { type ->
            if (type.startsWith("text/")) {
                intent.getStringExtra(Intent.EXTRA_TEXT)?.let { url ->
                    saveLinkToDatabase(url)
                }
            } else {
                intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)?.let { uri ->
                    savePdfToDatabase(context, uri)
                }
            }
        }
    }

    private suspend fun handleSendMultipleIntent(context: Context, intent: Intent) {
        intent.type?.let { type ->
            if (type.startsWith("text/")) {
                intent.getStringArrayListExtra(Intent.EXTRA_TEXT)?.let { urls ->
                    for (url in urls) {
                        saveLinkToDatabase(url)
                    }
                }
            } else {
                intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)?.let { uris ->
                    for (uri in uris) {
                        savePdfToDatabase(context, uri)
                    }
                }
            }
        }
    }

    private suspend fun savePdfToDatabase(context: Context, uri: Uri) {
        val filePath = savePdfToInternalStorage(context, uri)
        val docItem = DocItem(name = getFileName(context, uri), filepath = filePath)
        repository.insert(docItem)
    }

    private suspend fun saveLinkToDatabase(url: String) {
        val docItem = DocItem(name = url, filepath = url)
        repository.insert(docItem)
    }

    private suspend fun savePdfToInternalStorage(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, getFileName(context, uri))
        val outputStream = FileOutputStream(file)

        inputStream?.copyTo(outputStream)

        outputStream.close()
        inputStream?.close()

        return@withContext file.absolutePath
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
                result = result?.substring(cut + 1) ?: result
            }
        }
        return result!!
    }
}


