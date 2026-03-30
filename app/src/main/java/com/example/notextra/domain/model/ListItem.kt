package com.example.notextra.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "list_items",
    // ==========================================
    // RELASI ANTAR TABEL (FOREIGN KEY)
    // ==========================================
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            // CASCADE: Jika Note induknya dihapus, maka semua ListItem di dalamnya ikut terhapus otomatis
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("noteId")]
)
data class ListItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // ID unik untuk setiap item
    val noteId: Int, // Penghubung (Foreign Key) untuk mengetahui item ini milik Note/List yang mana
    val sequenceNumber: Int,
    val nama: String,
    val status: String = "Belum",
    val catatan1: String,
    val catatan2: String,
    val isChecked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    var dynamicData: String = "{}" // Akan menyimpan JSON dari Map<String, String> (Nama Kolom -> Isi)
)