package com.example.notextra.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.notextra.domain.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    // ==========================================
    // READ OPERATIONS (Operasi Membaca Data)
    // ==========================================
    /**Mengambil seluruh data catatan (gabungan Note dan List) dari database.*/
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<Note>>

    /**Mengambil data berdasarkan tipenya.*/
    @Query("SELECT * FROM notes WHERE noteType = :type ORDER BY timestamp DESC")
    fun getNotesByType(type: String): Flow<List<Note>>

    // ==========================================
    // WRITE OPERATIONS (Operasi Mengubah Data)
    // ==========================================
    /**Menyimpan Note/List baru ke database.*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    /**Memperbarui isi, judul, kategori, atau status Pinned dari Note/List yang sudah ada.*/
    @Update
    suspend fun updateNote(note: Note)

    /**Menghapus Note/List dari database secara permanen.*/
    @Delete
    suspend fun deleteNote(note: Note)
}