package com.example.notextra.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.notextra.data.repository.NoteRepository
import com.example.notextra.domain.model.ListItem
import com.example.notextra.domain.model.Note
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    val notes: StateFlow<List<Note>> = repository.getNotesByType("REGULAR")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val listNotes: StateFlow<List<Note>> = repository.getNotesByType("LIST")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addNote(title: String, content: String, noteType: String = "REGULAR") {
        viewModelScope.launch {
            val newNote = Note(
                title = title,
                content = content,
                timestamp = System.currentTimeMillis(),
                noteType = noteType
            )
            repository.insertNote(newNote)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun updateNote(note: Note, newTitle: String, newContent: String) {
        viewModelScope.launch {
            val updatedNote = note.copy(
                title = newTitle,
                content = newContent,
                timestamp = System.currentTimeMillis()
            )
            repository.updateNote(updatedNote)
        }
    }

    // --- List Item (Tabel) Functions ---

    fun getListItems(noteId: Int): StateFlow<List<ListItem>> {
        return repository.getItemsByNoteId(noteId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun addListItem(noteId: Int, nama: String, catatan1: String, catatan2: String) {
        viewModelScope.launch {
            val currentMax = repository.getMaxSequenceNumber(noteId) ?: 0
            val newItem = ListItem(
                noteId = noteId,
                sequenceNumber = currentMax + 1,
                nama = nama,
                catatan1 = catatan1,
                catatan2 = catatan2
            )
            repository.insertListItem(newItem)
        }
    }

    fun updateListItem(item: ListItem) {
        viewModelScope.launch {
            repository.updateListItem(item)
        }
    }
}

class NoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}