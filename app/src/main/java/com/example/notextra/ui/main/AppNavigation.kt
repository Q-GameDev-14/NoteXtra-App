package com.example.notextra.ui.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavigation(viewModel: NoteViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {

        // Rute 1: Layar Utama (Tab Note & List)
        composable("main") {
            MainScreen(
                viewModel = viewModel,
                onNavigateToEdit = { noteId ->
                    navController.navigate("edit/$noteId")
                },
                onNavigateToListDetail = { noteId ->
                    navController.navigate("table_detail/$noteId")
                }
            )
        }

        // Rute 2: Layar Edit Note Biasa
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

        // Rute 3: Layar Detail Tabel (List)
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
    }
}