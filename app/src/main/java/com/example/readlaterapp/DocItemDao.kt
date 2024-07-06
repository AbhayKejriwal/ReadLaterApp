package com.example.readlaterapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DocItemDao {

    @Insert
    suspend fun insert(doc: DocItem)

    @Delete
    suspend fun delete(doc: DocItem)

    @Query("SELECT * FROM doc_item ORDER BY id ASC")
    fun getAllDocs(): LiveData<List<DocItem>>

    @Query("SELECT * FROM doc_item WHERE is_archived = TRUE ORDER BY id ASC")
    fun getArchivedDocs(): LiveData<List<DocItem>>

    @Query("SELECT * FROM doc_item WHERE is_liked = TRUE ORDER BY id ASC")
    fun getStarredDocs(): LiveData<List<DocItem>>
}
