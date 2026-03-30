package com.example.notextra.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.notextra.domain.model.ListItem
import com.example.notextra.domain.model.Note

/**Konfigurasi utama Room Database untuk aplikasi Note Xtra.*/
@TypeConverters(Converters::class)
@Database(
    entities = [Note::class, ListItem::class],
    version = 4,        // Versi database saat ini
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {
    abstract val noteDao: NoteDao
    abstract val listItemDao: ListItemDao

    companion object {
        // ==========================================
        // SINGLETON INSTANCE & MIGRATIONS
        // ==========================================
        /**@Volatile memastikan bahwa nilai INSTANCE selalu terbarui dan terlihat oleh semua thread (proses) secara instan.*/
        @Volatile
        private var INSTANCE: NoteDatabase? = null
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE notes ADD COLUMN category TEXT NOT NULL DEFAULT 'Work'")
                db.execSQL("ALTER TABLE notes ADD COLUMN isPinned INTEGER NOT NULL DEFAULT 0")
            }
        }

        // ==========================================
        // DATABASE BUILDER
        // ==========================================
        /**Mengambil instance database. Jika belum ada, maka akan dibuat (inisialisasi).*/
        fun getInstance(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}