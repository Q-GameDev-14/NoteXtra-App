package com.example.notextra.ui.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

/**[AppNavigation] adalah komponen akar (root) yang mengatur sistem navigasi aplikasi.*/
@Composable
fun AppNavigation(viewModel: NoteViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        // ==========================================
        // RUTE 1: LAYAR UTAMA (Home Screen)
        // ==========================================
        composable("main") {
            MainScreen(
                viewModel = viewModel,
                onNavigateToEdit = { noteId ->
                    navController.navigate("edit/$noteId")
                },
                onNavigateToListDetail = { noteId ->
                    navController.navigate("table_detail/$noteId")
                },
                // --- TAMBAHAN BARU: Navigasi ke Pinned Notes ---
                onNavigateToPinnedNotes = {
                    navController.navigate("pinned_notes")
                }
            )
        }

        // ==========================================
        // RUTE 2: LAYAR EDIT NOTE (Teks Biasa)
        // ==========================================
        composable(
            route = "edit/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0

            EditNoteScreen(
                noteId = noteId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ==========================================
        // RUTE 3: LAYAR DETAIL TABEL (List)
        // ==========================================
        composable(
            route = "table_detail/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0

            ListDetailScreen(
                noteId = noteId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ==========================================
        // RUTE 4: LAYAR SEMUA PINNED NOTES (DESAIN B)
        // ==========================================
        composable("pinned_notes") {
            PinnedNotesListScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { noteId ->
                    navController.navigate("edit/$noteId")
                },
                onNavigateToListDetail = { noteId ->
                    navController.navigate("table_detail/$noteId")
                }
            )
        }
    }
}