package com.example.readlaterapp

import kotlinx.coroutines.flow.Flow

class DocItemRepository(private val docItemDao: DocItemDao) {

    val allDocs: Flow<List<DocItem>> = docItemDao.getDoc()

    suspend fun insert(docItem: DocItem) {
        docItemDao.insert(docItem)
    }

    suspend fun delete(docItem: DocItem) {
        docItemDao.delete(docItem)
    }
}
