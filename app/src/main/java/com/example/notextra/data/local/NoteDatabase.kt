package com.example.notextra.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.notextra.domain.model.ListItem
import com.example.notextra.domain.model.Note

@Database(
    entities = [Note::class, ListItem::class],
    version = 3,
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {
    abstract val noteDao: NoteDao
    abstract val listItemDao: ListItemDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Tambah kolom category
                db.execSQL("ALTER TABLE notes ADD COLUMN category TEXT NOT NULL DEFAULT 'Work'")
                // Tambah kolom isPinned (SQLite menyimpan Boolean sebagai angka 0 dan 1)
                db.execSQL("ALTER TABLE notes ADD COLUMN isPinned INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getInstance(context: Context): NoteDatabase {
            // Jika INSTANCE tidak null, kembalikan INSTANCE.
            // Jika null, buat database baru.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                    .addMigrations(MIGRATION_1_2) // 1. Prioritaskan jalur aman (data tidak hilang)
                    .fallbackToDestructiveMigration() // 2. Jaring pengaman (reset kalau error)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}