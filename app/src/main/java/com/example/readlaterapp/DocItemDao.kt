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

    @Query("SELECT * FROM DocItem WHERE archived = 0 ORDER BY id ASC")
    fun getDoc(): Flow<List<DocItem>>

    @Query("SELECT * FROM DocItem WHERE archived = 1 ORDER BY id ASC")
    fun getArchivedDocs(): Flow<List<DocItem>>
}
