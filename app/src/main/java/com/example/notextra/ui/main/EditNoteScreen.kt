package com.example.notextra.ui.main

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import com.example.notextra.ui.components.AppBaseDialog
import com.example.notextra.ui.theme.AppDeleteColor
import com.example.notextra.ui.theme.AppPrimaryColor
import com.example.notextra.ui.theme.BgColor
import com.example.notextra.ui.theme.FabColor
import com.example.notextra.ui.theme.TextDark
import com.example.notextra.ui.theme.TextGray
import com.example.notextra.utils.DateUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**Layar untuk mengedit isi catatan teks biasa (REGULAR).*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    noteId: Int,
    viewModel: NoteViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    // ==========================================
    // DATA & STATE MANAGEMENT
    // ==========================================
    val notes by viewModel.notes.collectAsState()
    val note = notes.find { it.id == noteId }

    // Membantu menutup keyboard dan mengatur animasi delay
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    // State untuk menampung teks yang sedang diketik
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    // State untuk menyimpan nilai asli saat pertama kali dibuka
    var originalTitle by remember { mutableStateOf("") }
    var originalContent by remember { mutableStateOf("") }

    // State penentu apakah dialog peringatan harus muncul
    var showSaveDialog by remember { mutableStateOf(false) }
    var showBackDialog by remember { mutableStateOf(false) }

    // Mengisi State dengan data dari Database saat layar pertama kali dirender
    LaunchedEffect(note) {
        if (note != null && originalTitle.isEmpty() && originalContent.isEmpty()) {
            title = note.title
            content = note.content
            originalTitle = note.title
            originalContent = note.content
        }
    }

    // Mendeteksi apakah user melakukan perubahan pada judul atau isi
    val hasChanges = title != originalTitle || content != originalContent

    // Fungsi khusus untuk menyimpan data ke database
    val performSave = {
        focusManager.clearFocus()
        if (note != null) {
            viewModel.updateNote(note, newTitle = title, newContent = content)
            Toast.makeText(context, "Note berhasil diupdate", Toast.LENGTH_SHORT).show()
            onNavigateBack()
        }
    }

    BackHandler {
        if (hasChanges) showBackDialog = true else onNavigateBack()
    }

    // ==========================================
    // UI UTAMA
    // ==========================================
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Note Xtra", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    Button(
                        onClick = { if (hasChanges) showBackDialog = true else onNavigateBack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F4FF), contentColor = FabColor),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.padding(start = 12.dp).height(36.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Back", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Back", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                },
                actions = {
                    Button(
                        onClick = { if (hasChanges) showSaveDialog = true else onNavigateBack() },
                        colors = ButtonDefaults.buttonColors(containerColor = FabColor, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        modifier = Modifier.padding(end = 12.dp).height(36.dp)
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            )
        },
        containerColor = BgColor
    ) { paddingValues ->

        // --- KONTEN EDIT NOTE ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BgColor)
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            BasicTextField(
                value = title,
                onValueChange = { title = it },
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextDark),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (title.isEmpty()) Text("Judul Note...", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note?.let { DateUtils.formatTimestamp(it.timestamp) } ?: "Tanggal Baru",
                color = TextGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
            androidx.compose.material3.HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(24.dp))

            androidx.compose.material3.OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FabColor,
                    unfocusedBorderColor = FabColor,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                ),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp, color = TextDark, lineHeight = 24.sp),
                placeholder = { Text("Start writing your note here. Tap anywhere to begin editing.", color = Color.Gray) }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // ==========================================
    // POP-UP DIALOG KONFIRMASI
    // ==========================================
    // Dialog 1: Saat user menekan tombol Save di pojok kanan atas
    if (showSaveDialog) {
        AppBaseDialog(
            title = "Simpan Perubahan",
            confirmText = "Simpan",
            dismissText = "Batal",
            onDismissRequest = { showSaveDialog = false },
            onConfirm = {
                showSaveDialog = false
                scope.launch { delay(150); performSave() }
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.padding(bottom = 16.dp).size(56.dp), tint = AppPrimaryColor)
                Text("Apakah Anda yakin ingin menyimpan perubahan?", color = Color.Black, textAlign = TextAlign.Center)
            }
        }
    }

    // Dialog 2: Saat user menekan tombol Back tapi ada teks yang berubah
    if (showBackDialog) {
        AppBaseDialog(
            title = "Perubahan Belum Disimpan",
            confirmText = "Simpan",
            dismissText = "Lanjut",
            discardText = "Buang",
            onDismissRequest = { showBackDialog = false },
            onConfirm = {
                showBackDialog = false
                scope.launch { delay(150); performSave() }
            },
            onDiscard = {
                showBackDialog = false
                onNavigateBack()
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.padding(bottom = 16.dp).size(56.dp), tint = AppDeleteColor)
                Text("Perubahan belum disimpan, apa yang ingin Anda lakukan?", color = Color.Black, textAlign = TextAlign.Center)
            }
        }
    }
}