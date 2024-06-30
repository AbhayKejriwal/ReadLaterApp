package com.example.readlaterapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class FileReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("FileReceiver", "Intent received: ${intent.action}")

        if (intent.action == Intent.ACTION_SEND || intent.action == Intent.ACTION_SEND_MULTIPLE) {
            val serviceIntent = Intent(context, FileProcessingService::class.java).apply {
                action = intent.action
                putExtras(intent.extras ?: return)
            }
            context.startService(serviceIntent)
        }
    }
}
