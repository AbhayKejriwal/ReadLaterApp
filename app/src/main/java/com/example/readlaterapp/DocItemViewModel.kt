package com.example.readlaterapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DocItemViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DocItemRepository = DocItemRepository(DocDatabase.getDatabase(application).docItemDao())

    val allDocs: LiveData<List<DocItem>> = repository.allDocs.asLiveData()
    val archivedDocs: LiveData<List<DocItem>> = repository.archivedDocs
    val starredDocs: LiveData<List<DocItem>> = repository.starredDocs

    fun insert(docItem: DocItem) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(docItem)
    }

    fun update(docItem: DocItem) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(docItem)
    }

    fun delete(docItem: DocItem) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(docItem)
    }
}
