package com.example.notextra.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateUtils {

    // Fungsi untuk mengubah timestamp (Long) menjadi String yang bisa dibaca
    fun formatTimestamp(timestamp: Long): String {
        // Karena Minimum SDK Anda API 29 (Android 10), kita bisa menggunakan
        // library java.time modern yang jauh lebih aman dan cepat.
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
            .withZone(ZoneId.systemDefault()) // Menggunakan zona waktu lokal HP user

        return formatter.format(Instant.ofEpochMilli(timestamp))
    }
}