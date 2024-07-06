package com.example.readlaterapp

import androidx.lifecycle.LiveData

class DocItemRepository(private val docItemDao: DocItemDao) {

    val allDocs: LiveData<List<DocItem>> = docItemDao.getAllDocs()
    val archivedDocs: LiveData<List<DocItem>> = docItemDao.getArchivedDocs()
    val starredDocs: LiveData<List<DocItem>> = docItemDao.getStarredDocs()

    suspend fun insert(docItem: DocItem) {
        docItemDao.insert(docItem)
    }

    suspend fun delete(docItem: DocItem) {
        docItemDao.delete(docItem)
    }

    suspend fun update(docItem: DocItem) {
        docItemDao.insert(docItem) // Room handles update since primary key is auto-generated
    }
}