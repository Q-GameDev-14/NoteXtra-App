package com.example.notextra.data.repository

import com.example.notextra.data.local.ListItemDao
import com.example.notextra.data.local.NoteDao
import com.example.notextra.domain.model.ListItem
import com.example.notextra.domain.model.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(
    private val noteDao: NoteDao,
    private val listItemDao: ListItemDao
) {
    // --- KUMPULAN FUNGSI UNTUK NOTE BIASA (Biarkan sama persis) ---
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()
    fun getNotesByType(type: String): Flow<List<Note>> = noteDao.getNotesByType(type)
    suspend fun insertNote(note: Note) = noteDao.insertNote(note)
    suspend fun updateNote(note: Note) = noteDao.updateNote(note)
    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)

    // --- TAMBAHKAN KUMPULAN FUNGSI BARU UNTUK LIST ITEM (TABEL) ---
    fun getItemsByNoteId(noteId: Int): Flow<List<ListItem>> = listItemDao.getItemsByNoteId(noteId)
    suspend fun getMaxSequenceNumber(noteId: Int): Int? = listItemDao.getMaxSequenceNumber(noteId)
    suspend fun insertListItem(item: ListItem) = listItemDao.insertItem(item)
    suspend fun updateListItem(item: ListItem) = listItemDao.updateItem(item)
    suspend fun deleteListItem(item: ListItem) = listItemDao.deleteItem(item)
}