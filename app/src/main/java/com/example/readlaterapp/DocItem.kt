package com.example.readlaterapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doc_item")
data class DocItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "document_name")
    val name: String,
    @ColumnInfo(name = "file_path")
    val filepath: String,
    @ColumnInfo(name = "is_liked")
    val like: Boolean = false,
    @ColumnInfo(name = "is_archived")
    val archived: Boolean = false
)
