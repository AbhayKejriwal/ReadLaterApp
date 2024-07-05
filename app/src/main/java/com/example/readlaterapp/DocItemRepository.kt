package com.example.readlaterapp

import kotlinx.coroutines.flow.Flow

class DocItemRepository(private val docItemDao: DocItemDao) {

    val allDocs: Flow<List<DocItem>> = docItemDao.getAllDocs()
    val archivedDocs: Flow<List<DocItem>> = docItemDao.getArchivedDocs()
    val starredDocs: Flow<List<DocItem>> = docItemDao.getStarredDocs()

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
