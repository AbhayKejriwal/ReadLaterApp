package com.example.readlaterapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData

class DocItemViewModel(private val repository: DocItemRepository) : ViewModel() {

    val allDocs: LiveData<List<DocItem>> = repository.allDocs.asLiveData()
    val archivedDocs: LiveData<List<DocItem>> = repository.archivedDocs.asLiveData()
    val starredDocs: LiveData<List<DocItem>> = repository.starredDocs.asLiveData()

    fun insert(docItem: DocItem) {
        viewModelScope.launch {
            repository.insert(docItem)
        }
    }

    fun delete(docItem: DocItem) {
        viewModelScope.launch {
            repository.delete(docItem)
        }
    }

    fun getStarredDocs(): LiveData<List<DocItem>> {
        return repository.starredDocs.asLiveData()
    }

    fun getArchivedDocs(): LiveData<List<DocItem>> {
        return repository.archivedDocs.asLiveData()
    }
}
