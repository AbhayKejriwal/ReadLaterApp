package com.example.readlaterapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DocItemDao {

    @Insert
    suspend fun insert(doc: DocItem)

    @Delete
    suspend fun delete(doc: DocItem)

    @Query("SELECT * FROM doc_item ORDER BY id ASC")
    fun getAllDocs(): Flow<List<DocItem>>

    @Query("SELECT * FROM doc_item WHERE is_archived = 1 ORDER BY id ASC")
    fun getArchivedDocs(): Flow<List<DocItem>>

    @Query("SELECT * FROM doc_item WHERE is_liked = 1 ORDER BY id ASC")
    fun getStarredDocs(): Flow<List<DocItem>>
}
