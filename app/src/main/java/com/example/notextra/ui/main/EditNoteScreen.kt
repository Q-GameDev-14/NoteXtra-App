package com.example.notextra.ui.main

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.notextra.ui.components.AppBaseDialog
import com.example.notextra.ui.theme.AppBackgroundColor
import com.example.notextra.ui.theme.AppDeleteColor
import com.example.notextra.ui.theme.AppHeaderColor
import com.example.notextra.ui.theme.AppPrimaryColor
import com.example.notextra.ui.theme.AppSecondaryColor
import com.example.notextra.utils.DateUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    noteId: Int,
    viewModel: NoteViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val notes by viewModel.notes.collectAsState()
    val note = notes.find { it.id == noteId }

    // BUG FIX: Tambahkan FocusManager dan CoroutineScope untuk transisi mulus
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var originalTitle by remember { mutableStateOf("") }
    var originalContent by remember { mutableStateOf("") }

    var showSaveDialog by remember { mutableStateOf(false) }
    var showBackDialog by remember { mutableStateOf(false) }

    LaunchedEffect(note) {
        if (note != null && originalTitle.isEmpty() && originalContent.isEmpty()) {
            title = note.title
            content = note.content
            originalTitle = note.title
            originalContent = note.content
        }
    }

    val hasChanges = title != originalTitle || content != originalContent

    // BUG FIX: Lepaskan fokus (turunkan keyboard) sebelum menyimpan
    val performSave = {
        focusManager.clearFocus()
        if (note != null) {
            viewModel.updateNote(note, title, content)
            Toast.makeText(context, "Note berhasil diupdate", Toast.LENGTH_SHORT).show()
            onNavigateBack()
        }
    }

    BackHandler {
        if (hasChanges) showBackDialog = true else onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nama Aplikasi", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppHeaderColor),
                navigationIcon = {
                    IconButton(onClick = { if (hasChanges) showBackDialog = true else onNavigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = { if (hasChanges) showSaveDialog = true else onNavigateBack() }) {
                        Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        containerColor = AppBackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppSecondaryColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        placeholder = { Text("Judul Note", color = Color.LightGray) }
                    )
                    if (note != null) {
                        Text(
                            text = DateUtils.formatTimestamp(note.timestamp),
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = AppPrimaryColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 10,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    placeholder = { Text("Isi Note......", color = Color.LightGray) }
                )
            }
        }
    }

    // --- DIALOG ---
    if (showSaveDialog) {
        AppBaseDialog(
            title = "Simpan Perubahan",
            confirmText = "Simpan",
            dismissText = "Batal",
            onDismissRequest = { showSaveDialog = false },
            onConfirm = {
                showSaveDialog = false
                // BUG FIX: Beri jeda 150ms agar animasi dialog selesai sebelum layar pindah
                scope.launch {
                    delay(150)
                    performSave()
                }
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.padding(bottom = 16.dp).height(56.dp).width(56.dp),
                    tint = AppPrimaryColor
                )
                Text(
                    text = "Apakah Anda yakin ingin menyimpan perubahan?",
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    if (showBackDialog) {
        AppBaseDialog(
            title = "Perubahan Belum Disimpan",
            confirmText = "Simpan",
            dismissText = "Lanjut", // Sesuai idemu
            discardText = "Buang",
            onDismissRequest = { showBackDialog = false }, // Aksi Lanjut
            onConfirm = { // Aksi Simpan
                showBackDialog = false
                scope.launch {
                    delay(150)
                    performSave()
                }
            },
            onDiscard = { // Aksi Buang (Baru)
                showBackDialog = false
                onNavigateBack() // Kembali tanpa memanggil performSave
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.padding(bottom = 16.dp).height(56.dp).width(56.dp),
                    tint = AppDeleteColor
                )
                Text(
                    text = "Perubahan belum disimpan, apa yang ingin Anda lakukan?",
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}