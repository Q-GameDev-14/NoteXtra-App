package com.example.notextra.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notextra.domain.model.Note
import com.example.notextra.ui.components.AllNoteCardItem
import com.example.notextra.ui.components.getCategoryColor
import com.example.notextra.ui.theme.BgColor
import com.example.notextra.ui.theme.FabColor
import com.example.notextra.ui.theme.TextDark
import com.example.notextra.ui.theme.TextGray
import com.example.notextra.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinnedNotesListScreen(
    viewModel: NoteViewModel,
    onNavigateBack: () -> Unit,             // <--- Ganti NavController jadi Callback
    onNavigateToEdit: (Int) -> Unit,        // <--- Callback buka Note
    onNavigateToListDetail: (Int) -> Unit   // <--- Callback buka Custom Table/List
) {
    // 1. Mengambil data dari ViewModel
    val notes by viewModel.notes.collectAsState()
    val listNotes by viewModel.listNotes.collectAsState()

    // 2. Menggabungkan dan memfilter note yang di-pin saja
    val allPinnedNotes = remember(notes, listNotes) {
        val regularPinned = notes.filter { it.isPinned }
        val listPinned = listNotes.filter { it.isPinned }
        (regularPinned + listPinned).sortedByDescending { it.timestamp }
    }

    // 3. State untuk Filter Kategori (Diperbarui dengan pemisah Note & List)
    val categories = remember(allPinnedNotes) {
        // Tambahkan opsi "Notes" dan "Lists" di awal, baru diikuti kategori dinamis
        listOf("All", "Notes", "Lists") + allPinnedNotes.map { it.category }.distinct()
    }
    var selectedCategory by remember { mutableStateOf("All") }

    // 4. Menerapkan Filter Pintar
    val displayedNotes = remember(allPinnedNotes, selectedCategory) {
        when (selectedCategory) {
            "All" -> allPinnedNotes
            "Notes" -> allPinnedNotes.filter { it.noteType != "LIST" && it.noteType != "CUSTOM_LIST" }
            "Lists" -> allPinnedNotes.filter { it.noteType == "LIST" || it.noteType == "CUSTOM_LIST" }
            else -> allPinnedNotes.filter { it.category == selectedCategory }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Pinned notes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(BgColor)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = allPinnedNotes.size.toString(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextGray
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    Button(
                        onClick = { onNavigateBack() }, // Navigasi kembali
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F4FF), contentColor = FabColor),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.padding(start = 12.dp).height(36.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Back", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Back", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                },
                actions = {
                    // Tombol Search (Dummy, belum berfungsi)
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = TextDark)
                    }
                }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ==========================================
            // A. BARIS FILTER KATEGORI (Segmented Controls)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgColor)
                    .padding(vertical = 12.dp)
            ) {
                // Tombol Panah Kiri
                IconButton(onClick = { /* TODO: Scroll filter to left */ }) {
                    Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "Prev", tint = TextGray, modifier = Modifier.size(16.dp))
                }

                // Area Filter yang bisa di-scroll (disini kita pakai row sederhana dulu)
                Row(
                    modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        val isSelected = category == selectedCategory
                        val chipBgColor = if (isSelected) FabColor else Color.White
                        val chipTextColor = if (isSelected) Color.White else TextGray
                        val chipBorderColor = if (isSelected) FabColor else Color(0xFFE5E7EB)

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(chipBgColor)
                                .border(1.dp, chipBorderColor, RoundedCornerShape(16.dp))
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = category,
                                color = chipTextColor,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                // Tombol Panah Kanan
                IconButton(onClick = { /* TODO: Scroll filter to right */ }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next", tint = TextGray, modifier = Modifier.size(16.dp))
                }
            }

            // ==========================================
            // B. LAZYCOLUMN: DAFTAR PINNED NOTES (DESAIN B)
            // ==========================================
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                if (displayedNotes.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(56.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(48.dp), tint = TextGray)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Tidak ada note yang di-pin di kategori ini.", color = TextGray, fontSize = 14.sp)
                        }
                    }
                } else {
                    items(displayedNotes, key = { it.id }) { note ->
                        PinnedNoteListItem(
                            note = note,
                            onClick = {
                                // <--- UBAH LOGIKA KLIK JADI INI
                                if (note.noteType == "LIST" || note.noteType == "CUSTOM_LIST") {
                                    onNavigateToListDetail(note.id)
                                } else {
                                    onNavigateToEdit(note.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PinnedNoteListItem(
    note: Note,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // A. Colored Status Dot (Sisi Kiri)
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(getCategoryColor(note.category))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // B. Area Konten (Judul & Info)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${DateUtils.formatTimestamp(note.timestamp)}  •  ${note.category}",
                    fontSize = 13.sp,
                    color = TextGray
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // C. Right Chevron Arrow (Sisi Kanan)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Open",
                modifier = Modifier.size(16.dp),
                tint = TextGray
            )
        }
    }
}