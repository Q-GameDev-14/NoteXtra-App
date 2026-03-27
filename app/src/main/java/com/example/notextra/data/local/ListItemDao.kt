package com.example.notextra.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.notextra.domain.model.ListItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ListItemDao {
    // ==========================================
    // READ OPERATIONS (Operasi Membaca Data)
    // ==========================================
    /**Mengambil seluruh baris item yang dimiliki oleh satu List tertentu.*/
    @Query("SELECT * FROM list_items WHERE noteId = :noteId ORDER BY createdAt DESC")
    fun getItemsByNoteId(noteId: Int): Flow<List<ListItem>>

    /**Mengambil nomor urut (sequenceNumber) paling besar dari sebuah List.*/
    @Query("SELECT MAX(sequenceNumber) FROM list_items WHERE noteId = :noteId")
    suspend fun getMaxSequenceNumber(noteId: Int): Int?

    // ==========================================
    // WRITE OPERATIONS (Operasi Mengubah Data)
    // ==========================================
    /**Menambahkan baris item baru ke dalam List.*/
    @Insert
    suspend fun insertItem(item: ListItem)

    /**Memperbarui data item yang sudah ada (misal: mengubah teks item atau status centangnya).*/
    @Update
    suspend fun updateItem(item: ListItem)

    /**Menghapus baris item dari dalam List.*/
    @Delete
    suspend fun deleteItem(item: ListItem)
}