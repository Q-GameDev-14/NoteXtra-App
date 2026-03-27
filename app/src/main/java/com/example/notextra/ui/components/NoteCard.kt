package com.example.notextra.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.notextra.domain.model.Note
import com.example.notextra.ui.theme.AppPrimaryColor
import com.example.notextra.utils.DateUtils

// TIDAK TERPAKAI PADA VERSI SEKARANG
/**
 * [NoteCard] adalah komponen UI untuk menampilkan ringkasan catatan (Desain Versi 1.0).
 * * Catatan Developer: Saat ini halaman utama (MainScreen) sudah beralih menggunakan
 * komponen 'AllNoteCardItem' dengan fitur Swipe-to-Delete. Komponen ini tetap
 * dipertahankan sebagai cadangan (fallback) atau untuk digunakan di halaman sekunder.
 *
 * @param note Objek data Note yang isinya akan ditampilkan (Judul dan Tanggal).
 * @param onDeleteClick Aksi ketika ikon tempat sampah diklik secara langsung.
 * @param onClick Aksi ketika keseluruhan area kartu diklik (misal: membuka detail catatan).
 * @param modifier Parameter standar Compose untuk mengatur ukuran atau padding luar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(
    note: Note,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // --- BUNGKUSAN UTAMA KARTU ---
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppPrimaryColor // Warna dasar kartu
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Efek bayangan agar menonjol
    ) {

        // --- ISI KARTU (Layout Menyamping / Horizontal) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // 1. BAGIAN KIRI: Teks Judul Catatan
            Text(
                text = note.title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,                     // Dibatasi 1 baris saja
                overflow = TextOverflow.Ellipsis, // Jika kepanjangan, diakhiri titik-titik (...)
                modifier = Modifier.weight(1f)    // Memakan sisa ruang kosong agar fleksibel
            )

            // 2. BAGIAN TENGAH: Teks Tanggal (Diubah dari format Unix ke Tanggal mudah dibaca)
            Text(
                text = DateUtils.formatTimestamp(note.timestamp),
                color = Color.White.copy(alpha = 0.7f), // Putih agak transparan agar tidak mendominasi
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // 3. BAGIAN KANAN: Tombol Hapus (Ikon Tong Sampah)
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Note",
                    tint = Color.White
                )
            }
        }
    }
}