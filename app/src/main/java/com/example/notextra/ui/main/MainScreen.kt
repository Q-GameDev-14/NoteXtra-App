package com.example.notextra.ui.main

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.notextra.domain.model.Note
import com.example.notextra.ui.components.AppBaseDialog
import com.example.notextra.ui.components.NoteCard
import com.example.notextra.ui.theme.AppBackgroundColor
import com.example.notextra.ui.theme.AppDeleteColor
import com.example.notextra.ui.theme.AppHeaderColor
import com.example.notextra.ui.theme.AppPrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: NoteViewModel,
    onNavigateToEdit: (Int) -> Unit,
    onNavigateToListDetail: (Int) -> Unit
) {
    val context = LocalContext.current
    val notes by viewModel.notes.collectAsState()
    val listNotes by viewModel.listNotes.collectAsState()

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var noteToDelete by remember { mutableStateOf<Note?>(null) }

    var showAddDialog by remember { mutableStateOf(false) }
    var showListOptionDialog by remember { mutableStateOf(false) }
    var showAddListTitleDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nama Aplikasi", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppHeaderColor),
                actions = {
                    if (selectedTab == 0) {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Note", tint = Color.White)
                        }
                    } else if (selectedTab == 1) {
                        IconButton(onClick = { showListOptionDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add List", tint = Color.White)
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = AppPrimaryColor) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Text("button 1", color = Color.White) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Text("button 2", color = Color.White) }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Text("button 3", color = Color.White) }
                )
            }
        },
        containerColor = AppBackgroundColor
    ) { paddingValues ->
        when (selectedTab) {
            0 -> {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(top = 8.dp)) {
                    items(items = notes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onDeleteClick = { noteToDelete = note },
                            onClick = { onNavigateToEdit(note.id) }
                        )
                    }
                }
            }
            1 -> {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(top = 8.dp)) {
                    items(items = listNotes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onDeleteClick = { noteToDelete = note },
                            onClick = { onNavigateToListDetail(note.id) }
                        )
                    }
                }
            }
            2 -> { Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) }
        }
    }

    // --- DIALOG AREA ---

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }

        AppBaseDialog(
            title = "Tambah Note Baru",
            confirmText = "Simpan",
            dismissText = "Batal",
            onDismissRequest = { showAddDialog = false },
            onConfirm = {
                if (title.isNotBlank()) {
                    viewModel.addNote(title, content, noteType = "REGULAR")
                    showAddDialog = false
                }
            }
        ) {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Isi Note") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showListOptionDialog) {
        var selectedTableType by remember { mutableStateOf("Default Table") }

        AppBaseDialog(
            title = "Tambah List",
            confirmText = "Buat",
            dismissText = "Batal",
            onDismissRequest = { showListOptionDialog = false },
            onConfirm = {
                if (selectedTableType == "Default Table") {
                    showListOptionDialog = false
                    showAddListTitleDialog = true
                } else {
                    Toast.makeText(context, "Fitur Custom Table belum tersedia", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Column {
                OutlinedButton(
                    onClick = { selectedTableType = "Default Table" },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedTableType == "Default Table") Color.LightGray.copy(alpha = 0.5f) else Color.Transparent
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (selectedTableType == "Default Table") AppHeaderColor else Color.Gray
                    )
                ) {
                    Text("Default Table", color = Color.Black)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { selectedTableType = "Custom Table" },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedTableType == "Custom Table") Color.LightGray.copy(alpha = 0.5f) else Color.Transparent
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (selectedTableType == "Custom Table") AppHeaderColor else Color.Gray
                    )
                ) {
                    Text("Custom Table", color = Color.Black)
                }
            }
        }
    }

    if (showAddListTitleDialog) {
        var title by remember { mutableStateOf("") }

        AppBaseDialog(
            title = "Nama List",
            confirmText = "Buat List",
            dismissText = "Batal",
            onDismissRequest = { showAddListTitleDialog = false },
            onConfirm = {
                if (title.isNotBlank()) {
                    viewModel.addNote(title, content = "", noteType = "LIST")
                    showAddListTitleDialog = false
                }
            }
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul List Baru") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (noteToDelete != null) {
        AppBaseDialog(
            title = "Konfirmasi Hapus",
            confirmText = "Hapus",
            dismissText = "Batal",
            onDismissRequest = { noteToDelete = null },
            onConfirm = {
                noteToDelete?.let { viewModel.deleteNote(it) }
                noteToDelete = null
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.padding(bottom = 16.dp).height(56.dp).width(56.dp),
                    tint = AppDeleteColor
                )
                Text(
                    text = "Apakah Anda yakin ingin menghapus '${noteToDelete?.title}'?",
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}