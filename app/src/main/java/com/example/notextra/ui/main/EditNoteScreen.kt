package com.example.notextra.ui.main

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.unit.sp
import com.example.notextra.ui.components.AppBaseDialog
import com.example.notextra.ui.theme.AppBackgroundColor
import com.example.notextra.ui.theme.AppDeleteColor
import com.example.notextra.ui.theme.AppHeaderColor
import com.example.notextra.ui.theme.AppPrimaryColor
import com.example.notextra.ui.theme.AppSecondaryColor
import com.example.notextra.ui.theme.BgColor
import com.example.notextra.ui.theme.FabColor
import com.example.notextra.ui.theme.TextDark
import com.example.notextra.ui.theme.TextGray
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
            CenterAlignedTopAppBar(
                title = {
                    Text("Note Xtra", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
                },
                // Background TopBar kita buat Putih agar tombol birunya menonjol (sesuai gambar Claude)
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    // Tombol BACK (Style biru pudar / Light Blue)
                    Button(
                        onClick = { if (hasChanges) showBackDialog = true else onNavigateBack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF0F4FF), // Biru sangat pudar
                            contentColor = FabColor             // Teks biru tua
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.padding(start = 12.dp).height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Back", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                },
                actions = {
                    // Tombol SAVE (Style biru solid)
                    androidx.compose.material3.Button(
                        onClick = { if (hasChanges) showSaveDialog = true else onNavigateBack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FabColor, // Biru solid
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        modifier = Modifier.padding(end = 12.dp).height(36.dp)
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            )
        },
        containerColor = AppBackgroundColor
    ) { paddingValues ->
// --- ISI LAYAR EDIT NOTE ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BgColor) // Menggunakan background cerah
                .padding(horizontal = 24.dp, vertical = 24.dp) // Padding lebih lega
        ) {
            // TAHAP 2: JUDUL NOTE & TANGGAL (Desain Clean)
            // Memakai BasicTextField agar transparan tanpa garis bawah bawaan Material
            BasicTextField(
                value = title,
                onValueChange = { title = it },
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 32.sp, // Judul besar dan tebal
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                ),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (title.isEmpty()) {
                        Text("Judul Note...", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tanggal Edit
            Text(
                text = note?.let { DateUtils.formatTimestamp(it.timestamp) } ?: "Tanggal Baru",
                color = TextGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Garis tipis pemisah antara header dan isi note
            androidx.compose.material3.HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(24.dp))

            // TAHAP 3: KOTAK ISI NOTE (Desain Rounded Outline Biru)
            androidx.compose.material3.OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Membiarkan kotak ini memanjang mengisi sisa layar ke bawah
                shape = RoundedCornerShape(16.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FabColor,     // Garis pinggir biru saat diketik
                    unfocusedBorderColor = FabColor,   // Garis pinggir biru saat diam
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 16.sp,
                    color = TextDark,
                    lineHeight = 24.sp // Jarak antar baris agar nyaman dibaca
                ),
                placeholder = {
                    Text(
                        "Start writing your note here. Tap anywhere to begin editing.",
                        color = Color.Gray
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
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