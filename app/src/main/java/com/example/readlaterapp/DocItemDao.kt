package com.example.readlaterapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DocItemDao {

    @Insert
    suspend fun insert(doc: DocItem)

    @Delete
    suspend fun delete(doc: DocItem)

    @Update
    suspend fun update(doc: DocItem)

    @Query("SELECT * FROM doc_item WHERE is_archived = 0 ORDER BY id ASC")
    fun getAllDocs(): Flow<List<DocItem>>

    @Query("SELECT * FROM doc_item WHERE is_archived = 1 ORDER BY id ASC")
    fun getArchivedDocs(): Flow<List<DocItem>>

    @Query("SELECT * FROM doc_item WHERE is_liked = 1 ORDER BY id ASC")
    fun getStarredDocs(): Flow<List<DocItem>>

    @Query("SELECT * FROM doc_item WHERE is_archived = 1 ORDER BY id ASC")
    fun getArchivedDocsLiveData(): LiveData<List<DocItem>>

    @Query("SELECT * FROM doc_item WHERE is_liked = 1 ORDER BY id ASC")
    fun getStarredDocsLiveData(): LiveData<List<DocItem>>
}
