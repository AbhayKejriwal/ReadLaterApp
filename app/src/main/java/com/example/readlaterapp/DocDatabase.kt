package com.example.readlaterapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DocItem::class], version = 2, exportSchema = false)
abstract class DocDatabase : RoomDatabase() {

    abstract fun docItemDao(): DocItemDao

    companion object {
        @Volatile
        private var INSTANCE: DocDatabase? = null

        fun getDatabase(context: Context): DocDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DocDatabase::class.java,
                    "doc_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
