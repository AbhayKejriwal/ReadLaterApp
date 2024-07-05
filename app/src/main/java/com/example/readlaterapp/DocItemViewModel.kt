package com.example.readlaterapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DocItemViewModel(private val repository: DocItemRepository) : ViewModel() {

    val allDocs = repository.allDocs

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
}