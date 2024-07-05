package com.example.readlaterapp

import android.app.Application

class DocApp : Application() {

    override fun onCreate() {
        super.onCreate()
        DocDatabase.getDatabase(this)
    }
}
