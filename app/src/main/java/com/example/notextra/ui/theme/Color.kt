package com.example.notextra.ui.theme

import androidx.compose.ui.graphics.Color

// ====================================================
// WARNA UTAMA APLIKASI (DESAIN NOTE XTRA TERBARU)
// ====================================================
val BgColor = Color(0xFFF8F9FA)           // Background utama
val TextDark = Color(0xFF1B1B2F)          // Warna teks utama
val TextGray = Color(0xFF8E8E93)          // Warna teks sekunder
val FabColor = Color(0xFF2954D1)          // Biru utama untuk tombol aksi penting
val CardSurface = Color(0xFFFFFFFF)       // Putih murni untuk background Card biasa

// Warna dinamis untuk penanda kategori dan Pinned Card
val CardBlue = Color(0xFF3B82F6)          // Biru (Untuk kategori "Work" atau default)
val CardGreen = Color(0xFF10B981)         // Hijau (Untuk kategori "Personal")
val CardOrange = Color(0xFFF59E0B)        // Oranye (Untuk kategori "Organization")
/** Catatan: Kategori "Idea" menggunakan TableAccentColor (Ungu) di bawah */

// ====================================================
// WARNA STATUS ITEM (TABEL / LIST)
// ====================================================
val StatusSelesaiColor = Color(0xFF4CAF50)     // Hijau (Sukses/Selesai)
val StatusInProgressColor = Color(0xFFFF9800)  // Oranye (Sedang Dikerjakan)
val StatusDitundaColor = Color(0xFFEF4444)     // Merah (Tertunda/Bermasalah)
val StatusBelumColor = Color(0xFF7986CB)       // Ungu/Biru Netral (Default/Belum dimulai)

// ====================================================
// PALET TEMA LAMA (LEGACY / LAYAR SEKUNDER)
// ====================================================
// Saat ini masih digunakan oleh ListDetailScreen dan AppBaseDialog bawaan.
// Suatu saat jika layar tersebut sudah di-remake, palet ini bisa dihapus.

// ----------------------------------------------------
// PALET AKTIF: OCEAN TEAL (Clean Light Theme)
// ----------------------------------------------------
val AppHeaderColor = Color(0xFF0284C7)       // TopBar Header
val AppBackgroundColor = Color(0xFFF0F9FF)   // Background bawaan Scaffold lama
val AppPrimaryColor = Color(0xFF0EA5E9)      // Warna dasar komponen/Card lama
val AppSecondaryColor = Color(0xFF06B6D4)    // Aksen sekunder (Misal: Header Judul Card)
val AppDeleteColor = Color(0xFFEF4444)       // Aksen peringatan bahaya/hapus

val TableAccentColor = Color(0xFF06B6D4)     // Aksen tabel (Tombol Filter/Sort/Tambah)
val TableHeaderColor = Color(0xFF0284C7)     // Latar belakang header baris tabel

val DialogBackgroundColor = Color(0xFFFFFFFF) // Latar pop-up dialog lama
val DialogButtonColor = Color(0xFF0EA5E9)     // Tombol pop-up dialog lama

// ----------------------------------------------------
// PALET NONAKTIF: MIDNIGHT INDIGO (Premium Dark Theme)
// ----------------------------------------------------
/*
val AppHeaderColor = Color(0xFF4F46B8)
val AppBackgroundColor = Color(0xFF0D0D18)
val AppPrimaryColor = Color(0xFF16162A)
val AppSecondaryColor = Color(0xFF4F46B8)
val AppDeleteColor = Color(0xFFEF4444)

val TableAccentColor = Color(0xFFA78BFA)
val TableHeaderColor = Color(0xFF7C6FF7)

val DialogBackgroundColor = Color(0xFF16162A)
val DialogButtonColor = Color(0xFF7C6FF7)
*/

// ----------------------------------------------------
// WARNA DEFAULT BAWAAN COMPOSE
// ----------------------------------------------------
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)