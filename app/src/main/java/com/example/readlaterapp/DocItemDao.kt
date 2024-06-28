package com.example.readlaterapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DocItemDao {
    @Insert
    suspend fun insert(Doc : DocItem)

    @Delete
    suspend fun delete(doc: DocItem)

    @Query("SELECT * from DocItem ORDER BY id ASC")
    fun getDoc() : Flow<List<DocItem>>
}