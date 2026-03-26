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

    @Query("SELECT * FROM list_items WHERE noteId = :noteId ORDER BY createdAt DESC")
    fun getItemsByNoteId(noteId: Int): Flow<List<ListItem>>

    // Mencari nomor urut paling besar di satu List.
    @Query("SELECT MAX(sequenceNumber) FROM list_items WHERE noteId = :noteId")
    suspend fun getMaxSequenceNumber(noteId: Int): Int?

    @Insert
    suspend fun insertItem(item: ListItem)

    @Update
    suspend fun updateItem(item: ListItem)

    @Delete
    suspend fun deleteItem(item: ListItem)
}