package com.example.notextra.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "list_items",
    // Membuat relasi: Setiap ListItem dimiliki oleh satu Note
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"], // ID dari tabel notes
            childColumns = ["noteId"], // Disambungkan ke kolom noteId di tabel ini
            onDelete = ForeignKey.CASCADE
        )
    ],
    // Index membantu mempercepat pencarian data saat list mulai berisi ribuan baris
    indices = [Index("noteId")]
)
data class ListItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val noteId: Int,             // Penghubung ke Note mana list ini berada
    val sequenceNumber: Int,     // Nomor urut absolut (1, 2, ... 11) sesuai request Anda
    val nama: String,
    val status: String = "Belum", // Default value
    val catatan1: String,
    val catatan2: String,        // Akan kita fungsikan sebagai Link nanti
    val isChecked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis() // Penentu urutan (yang terbaru akan ditaruh di atas)
)