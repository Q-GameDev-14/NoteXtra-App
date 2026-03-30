package com.example.notextra.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    // ID unik untuk setiap Note (Dibuat otomatis oleh sistem berurutan: 1, 2, 3...)
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val timestamp: Long,
    val noteType: String = "REGULAR",
    val category: String = "Work",
    val isPinned: Boolean = false,
    var customColumns: String = "[]" // Akan menyimpan JSON dari List<ColumnConfig>
)