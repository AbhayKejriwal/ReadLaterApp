package com.example.readlaterapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DocItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "Document name")
    val name: String,
    @ColumnInfo(name = "filepath")
    val filepath: String
)
