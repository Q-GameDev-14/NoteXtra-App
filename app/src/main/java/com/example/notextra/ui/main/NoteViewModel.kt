package com.example.notextra.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.notextra.data.repository.NoteRepository
import com.example.notextra.domain.model.ListItem
import com.example.notextra.domain.model.Note
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**[NoteViewModel] berfungsi sebagai jembatan antara tampilan UI dan database (Repository).*/
class NoteViewModel(private val repository: NoteRepository) : ViewModel() {
    // ==========================================
    // 1. STATE MANAGEMENT (Aliran Data ke UI)
    // ==========================================
    val notes: StateFlow<List<Note>> = repository.getNotesByType("REGULAR")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val listNotes: StateFlow<List<Note>> = repository.getAllNotes()
        .map { allNotes ->
            allNotes.filter { it.noteType == "LIST" || it.noteType == "CUSTOM_LIST" }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ==========================================
    // 2. FUNGSI UNTUK NOTE UTAMA (Induk)
    // ==========================================
    fun addNote(title: String, content: String, noteType: String = "REGULAR", category: String = "Work", isPinned: Boolean = false, customColumnsJson: String = "[]") {
        viewModelScope.launch {
            val newNote = Note(
                title = title,
                content = content,
                timestamp = System.currentTimeMillis(),
                noteType = noteType,
                category = category,
                isPinned = isPinned,
                customColumns = customColumnsJson
            )
            repository.insertNote(newNote)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun updateNote(note: Note, newTitle: String, newContent: String, newCategory: String = note.category, newIsPinned: Boolean = note.isPinned) {
        viewModelScope.launch {
            val updatedNote = note.copy(
                title = newTitle,
                content = newContent,
                timestamp = System.currentTimeMillis(),
                category = newCategory,
                isPinned = newIsPinned
            )
            repository.updateNote(updatedNote)
        }
    }

    // ==========================================
    // 3. FUNGSI UNTUK LIST ITEM (Anak / Isi Tabel)
    // ==========================================
    fun getListItems(noteId: Int): StateFlow<List<ListItem>> {
        return repository.getItemsByNoteId(noteId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun addListItem(
        noteId: Int,
        nama: String = "-",
        catatan1: String = "-",
        catatan2: String = "-",
        dynamicDataJson: String = "{}" // <--- TAMBAHAN BARU: Parameter untuk nerima JSON
    ) {
        viewModelScope.launch {
            val currentMax = repository.getMaxSequenceNumber(noteId) ?: 0
            val newItem = ListItem(
                noteId = noteId,
                sequenceNumber = currentMax + 1, // Penomoran otomatis berurutan
                nama = nama,
                catatan1 = catatan1,
                catatan2 = catatan2,
                dynamicData = dynamicDataJson // <--- MASUKKAN JSON KE ENTITY DATABASE
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

// ==========================================
// 4. FACTORY CLASS
// ==========================================
/**[NoteViewModelFactory] bertugas sebagai "Pabrik" pembuat NoteViewModel.*/
class NoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}