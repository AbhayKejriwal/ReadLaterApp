package com.example.readlaterapp

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class DocItemRepository(private val docItemDao: DocItemDao) {

    val allDocs: Flow<List<DocItem>> = docItemDao.getAllDocs()
    val archivedDocs: LiveData<List<DocItem>> = docItemDao.getArchivedDocsLiveData()
    val starredDocs: LiveData<List<DocItem>> = docItemDao.getStarredDocsLiveData()

    suspend fun insert(docItem: DocItem) {
        docItemDao.insert(docItem)
    }

    suspend fun delete(docItem: DocItem) {
        docItemDao.delete(docItem)
    }

    suspend fun update(docItem: DocItem) {
        docItemDao.update(docItem)
    }
}
