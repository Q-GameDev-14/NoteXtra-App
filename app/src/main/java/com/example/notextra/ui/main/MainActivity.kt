package com.example.notextra.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notextra.data.local.NoteDatabase
import com.example.notextra.data.repository.NoteRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Membuat instance Database
        val database = NoteDatabase.getInstance(applicationContext)

        // 2. Membuat Repository dengan memasukkan DAO dari database
        val repository = NoteRepository(database.noteDao, database.listItemDao)

        // 3. Membuat Factory untuk merakit ViewModel
        val factory = NoteViewModelFactory(repository)

        setContent {
            // 4. Membuat/Mendapatkan instance ViewModel di dalam scope Jetpack Compose
            val viewModel: NoteViewModel = viewModel(factory = factory)

            // 5. Menampilkan layar utama
            AppNavigation(viewModel = viewModel)
        }
    }
}