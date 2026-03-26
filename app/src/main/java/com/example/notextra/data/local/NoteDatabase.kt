package com.example.notextra.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notextra.domain.model.ListItem
import com.example.notextra.domain.model.Note

@Database(
    entities = [Note::class, ListItem::class],
    version = 2,
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {
    abstract val noteDao: NoteDao
    abstract val listItemDao: ListItemDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getInstance(context: Context): NoteDatabase {
            // Jika INSTANCE tidak null, kembalikan INSTANCE.
            // Jika null, buat database baru.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}