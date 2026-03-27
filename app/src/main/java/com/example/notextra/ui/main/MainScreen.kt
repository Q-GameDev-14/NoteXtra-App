package com.example.notextra.ui.main

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.notextra.domain.model.Note
import com.example.notextra.ui.components.AppBaseDialog
import com.example.notextra.ui.theme.*
import com.example.notextra.utils.DateUtils

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
    var noteToQuickEdit by remember { mutableStateOf<Note?>(null) }

    var showAddDialog by remember { mutableStateOf(false) }
    var showListOptionDialog by remember { mutableStateOf(false) }
    var showAddListTitleDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Note Xtra", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = TextDark)
                },
                // Background TopBar menjadi transparan agar menyatu dengan background aplikasi
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgColor),
                actions = {
                    // Tombol Search (UI Mode)
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = TextDark, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // Tombol More (UI Mode)
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = TextDark, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 16.dp, // Efek bayangan ke atas
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_edit), contentDescription = "Notes", modifier = Modifier.size(24.dp)) },
                    label = { Text("Notes", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FabColor, selectedTextColor = FabColor, indicatorColor = Color.Transparent,
                        unselectedIconColor = TextGray, unselectedTextColor = TextGray
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.List, contentDescription = "List") },
                    label = { Text("List", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FabColor, selectedTextColor = FabColor, indicatorColor = Color.Transparent,
                        unselectedIconColor = TextGray, unselectedTextColor = TextGray
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FabColor, selectedTextColor = FabColor, indicatorColor = Color.Transparent,
                        unselectedIconColor = TextGray, unselectedTextColor = TextGray
                    )
                )
            }
        },
        floatingActionButton = {
            // FAB Biru Besar di Kanan Bawah
            if (selectedTab != 2) {
                FloatingActionButton(
                    onClick = { if (selectedTab == 0) showAddDialog = true else if (selectedTab == 1) showListOptionDialog = true },
                    containerColor = FabColor,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(20.dp), // Agak membulat bukan lingkaran penuh
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(32.dp))
                }
            }
        },
        containerColor = BgColor
    ) { paddingValues ->
        when (selectedTab) {
            0 -> {
                // --- TAB 0: NOTE ---
                LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

                    // Pisahkan data yang di-pin dan tidak
                    val pinnedNotes = notes.filter { it.isPinned }
                    val unpinnedNotes = notes.filter { !it.isPinned }

                    // SECTION 1: PINNED NOTES (Layout Grid 2x2)
                    if (pinnedNotes.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Header Pinned
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 12.dp)) {
                                    Text("📌", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("PINNED", color = TextGray, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.5.sp)
                                }

                                // Ambil maksimal 4 data, lalu belah per 2 item (chunked)
                                val displayPinned = pinnedNotes.take(4)
                                val chunkedPinned = displayPinned.chunked(2)

                                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                                    chunkedPinned.forEach { rowItems ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            rowItems.forEach { note ->
                                                PinnedNoteCardMockup(
                                                    modifier = Modifier.weight(1f), // Membagi lebar sama rata
                                                    note = note,
                                                    bgColor = getCategoryColor(note.category),
                                                    onClick = { onNavigateToEdit(note.id) },
                                                    onLongClick = { noteToQuickEdit = note }
                                                )
                                            }
                                            // Jika baris terakhir cuma ada 1 item, beri ruang kosong di sebelahnya
                                            if (rowItems.size == 1) {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(12.dp)) // Jarak antar baris ke bawah
                                    }
                                }

                                // Tombol "Tampilkan lebih banyak >>" jika data lebih dari 4
                                if (pinnedNotes.size > 4) {
                                    Text(
                                        text = "Tampilkan lebih banyak >>",
                                        color = FabColor, // Memakai warna biru Fab agar terlihat bisa diklik
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { /* TODO: Arahkan ke screen baru nanti */ }
                                            .padding(vertical = 8.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // SECTION 2: ALL NOTES
                    item {
                        Text("ALL NOTES", color = TextGray, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.5.sp, modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 8.dp))
                    }

                    items(items = unpinnedNotes, key = { it.id }) { note ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) { noteToDelete = note; false } else false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                val color by animateColorAsState(if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) AppDeleteColor else Color.Transparent)
                                Box(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 6.dp).clip(RoundedCornerShape(16.dp)).background(color).padding(end = 20.dp), contentAlignment = Alignment.CenterEnd) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                                }
                            },
                            content = {
                                AllNoteCardItem(
                                    note = note,
                                    barColor = getCategoryColor(note.category), // <--- Warnanya dinamis
                                    onClick = { onNavigateToEdit(note.id) },
                                    onLongClick = { noteToQuickEdit = note }
                                )
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
            1 -> {
                // --- TAB 1: LIST ---
                LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

                    // Pisahkan data yang di-pin dan tidak
                    val pinnedLists = listNotes.filter { it.isPinned }
                    val unpinnedLists = listNotes.filter { !it.isPinned }

                    // SECTION 1: PINNED NOTES (Layout Grid 2x2)
                    if (pinnedLists.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Header Pinned
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 12.dp)) {
                                    Text("📌", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("PINNED", color = TextGray, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.5.sp)
                                }

                                // Ambil maksimal 4 data, lalu belah per 2 item (chunked)
                                val displayPinned = pinnedLists.take(4)
                                val chunkedPinned = displayPinned.chunked(2)

                                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                                    chunkedPinned.forEach { rowItems ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            rowItems.forEach { note ->
                                                PinnedNoteCardMockup(
                                                    modifier = Modifier.weight(1f), // Membagi lebar sama rata
                                                    note = note,
                                                    bgColor = getCategoryColor(note.category),
                                                    onClick = { onNavigateToListDetail(note.id) },
                                                    onLongClick = { noteToQuickEdit = note }
                                                )
                                            }
                                            // Jika baris terakhir cuma ada 1 item, beri ruang kosong di sebelahnya
                                            if (rowItems.size == 1) {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(12.dp)) // Jarak antar baris ke bawah
                                    }
                                }

                                // Tombol "Tampilkan lebih banyak >>" jika data lebih dari 4
                                if (pinnedLists.size > 4) {
                                    Text(
                                        text = "Tampilkan lebih banyak >>",
                                        color = FabColor, // Memakai warna biru Fab agar terlihat bisa diklik
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { /* TODO: Arahkan ke screen baru nanti */ }
                                            .padding(vertical = 8.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // SECTION 2: ALL NOTES
                    item {
                        Text("ALL LIST", color = TextGray, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.5.sp, modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 8.dp))
                    }

                    items(items = unpinnedLists, key = { it.id }) { note ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) { noteToDelete = note; false } else false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                val color by animateColorAsState(if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) AppDeleteColor else Color.Transparent)
                                Box(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 6.dp).clip(RoundedCornerShape(16.dp)).background(color).padding(end = 20.dp), contentAlignment = Alignment.CenterEnd) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                                }
                            },
                            content = {
                                AllNoteCardItem(
                                    note = note,
                                    barColor = getCategoryColor(note.category), // <--- Warnanya dinamis
                                    onClick = { onNavigateToListDetail(note.id) },
                                    onLongClick = { noteToQuickEdit = note }
                                )
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
            2 -> { Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) }
        }
    }

    // --- DIALOG AREA (SAMA PERSIS SEPERTI SEBELUMNYA) ---

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        AppBaseDialog(
            title = "Tambah Note Baru", confirmText = "Simpan", dismissText = "Batal",
            onDismissRequest = { showAddDialog = false },
            onConfirm = { if (title.isNotBlank()) { viewModel.addNote(title, content, noteType = "REGULAR"); showAddDialog = false } }
        ) {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Isi Note") }, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    if (showListOptionDialog) {
        var selectedTableType by remember { mutableStateOf("Default Table") }
        AppBaseDialog(
            title = "Tambah List", confirmText = "Buat", dismissText = "Batal",
            onDismissRequest = { showListOptionDialog = false },
            onConfirm = {
                if (selectedTableType == "Default Table") { showListOptionDialog = false; showAddListTitleDialog = true }
                else { Toast.makeText(context, "Fitur Custom Table belum tersedia", Toast.LENGTH_SHORT).show() }
            }
        ) {
            Column {
                OutlinedButton(
                    onClick = { selectedTableType = "Default Table" }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selectedTableType == "Default Table") Color.LightGray.copy(alpha = 0.5f) else Color.Transparent),
                    border = BorderStroke(width = 1.dp, color = if (selectedTableType == "Default Table") AppHeaderColor else Color.Gray)
                ) { Text("Default Table", color = Color.Black) }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { selectedTableType = "Custom Table" }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selectedTableType == "Custom Table") Color.LightGray.copy(alpha = 0.5f) else Color.Transparent),
                    border = BorderStroke(width = 1.dp, color = if (selectedTableType == "Custom Table") AppHeaderColor else Color.Gray)
                ) { Text("Custom Table", color = Color.Black) }
            }
        }
    }

    if (showAddListTitleDialog) {
        var title by remember { mutableStateOf("") }
        AppBaseDialog(
            title = "Nama List", confirmText = "Buat List", dismissText = "Batal",
            onDismissRequest = { showAddListTitleDialog = false },
            onConfirm = { if (title.isNotBlank()) { viewModel.addNote(title, content = "", noteType = "LIST"); showAddListTitleDialog = false } }
        ) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul List Baru") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
    }

    if (noteToDelete != null) {
        AppBaseDialog(
            title = "Konfirmasi Hapus", confirmText = "Hapus", dismissText = "Batal",
            onDismissRequest = { noteToDelete = null },
            onConfirm = { noteToDelete?.let { viewModel.deleteNote(it) }; noteToDelete = null }
        ) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Default.Info, contentDescription = null, modifier = Modifier.padding(bottom = 16.dp).height(56.dp).width(56.dp), tint = AppDeleteColor)
                Text(text = "Apakah Anda yakin ingin menghapus '${noteToDelete?.title}'?", color = Color.Black, textAlign = TextAlign.Center)
            }
        }
    }

    // Tampilkan Dialog Quick Edit saat Note ditahan
    if (noteToQuickEdit != null) {
        QuickEditNoteDialog(
            note = noteToQuickEdit!!,
            onDismiss = { noteToQuickEdit = null },
            onSave = { judulBaru, kategoriBaru, isPinnedBaru ->
                viewModel.updateNote(
                    note = noteToQuickEdit!!,
                    newTitle = judulBaru,
                    newContent = noteToQuickEdit!!.content,
                    newCategory = kategoriBaru, // <--- Data baru masuk
                    newIsPinned = isPinnedBaru  // <--- Data baru masuk
                )
                noteToQuickEdit = null
            }
        )
    }
}

// =======================================================
// KOMPONEN UI BARU (KHUSUS DESAIN NOTE XTRA)
// =======================================================
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
// 1. Tambahkan parameter modifier
fun PinnedNoteCardMockup(modifier: Modifier = Modifier, note: Note, bgColor: Color, onClick: () -> Unit, onLongClick: () -> Unit = {}) {
    Box(
        modifier = modifier // 2. Gunakan modifier dari parameter
            .height(110.dp) // Tingginya tetap
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = note.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${DateUtils.formatTimestamp(note.timestamp)} · ${note.category}",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllNoteCardItem(note: Note, barColor: Color, onClick: () -> Unit, onLongClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Garis vertikal warna
            Box(modifier = Modifier.width(4.dp).height(32.dp).clip(RoundedCornerShape(4.dp)).background(barColor))

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    color = TextDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    // Simulasi Kategori (Bisa diganti nanti)
                    text = "${DateUtils.formatTimestamp(note.timestamp)} · ${note.category}",
                    color = TextGray,
                    fontSize = 12.sp
                )
            }

            // Ikon Panah Kanan
            Icon(
                imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Open",
                tint = Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickEditNoteDialog(
    note: Note,
    onDismiss: () -> Unit,
    onSave: (String, String, Boolean) -> Unit // (Judul, Kategori, isPinned)
) {
    var judul by remember { mutableStateOf(note.title) }
    var kategori by remember { mutableStateOf(note.category) }
    var isPinned by remember { mutableStateOf(note.isPinned) }
    var expandedKategori by remember { mutableStateOf(false) }
    val opsiKategori = listOf("Work", "Personal", "Organization", "Idea")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE5E7EB)) // Abu-abu terang sesuai gambarmu
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Judul Pop-up (Menampilkan judul asli note)
                Text(
                    text = note.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // 1. Kotak Input Judul (Style Gambar 2)
                Text("Judul Note", color = TextDark, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp, start = 4.dp))
                OutlinedTextField(
                    value = judul,
                    onValueChange = { judul = it },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = outlinedTextFieldColors(
                        focusedBorderColor = TextDark, unfocusedBorderColor = Color.Gray, containerColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Kotak Dropdown Kategori
                Text("Kategori", color = TextDark, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp, start = 4.dp))
                Box {
                    OutlinedTextField(
                        value = kategori,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        trailingIcon = {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Dropdown", modifier = Modifier.clickable { expandedKategori = true })
                        },
                        colors = outlinedTextFieldColors(
                            focusedBorderColor = TextDark, unfocusedBorderColor = Color.Gray, containerColor = Color.Transparent
                        )
                    )
                    DropdownMenu(expanded = expandedKategori, onDismissRequest = { expandedKategori = false }) {
                        opsiKategori.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = { kategori = opt; expandedKategori = false }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Tombol Toggle Pinned
                OutlinedButton(
                    onClick = { isPinned = !isPinned },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Gray),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = if (isPinned) TableAccentColor.copy(alpha = 0.2f) else Color.Transparent)
                ) {
                    Text(if (isPinned) "Ter-Pinned 📌" else "Pinned", color = TextDark)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 4. Action Buttons (Batal & Simpan)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7986CB))
                    ) { Text("Batal") }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = { onSave(judul, kategori, isPinned) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7986CB))
                    ) { Text("Simpan") }
                }
            }
        }
    }
}

// Fungsi untuk mengubah nama Kategori menjadi Warna
fun getCategoryColor(category: String): Color {
    return when(category) {
        "Work" -> CardBlue
        "Personal" -> CardGreen
        "Organization" -> CardOrange
        "Idea" -> TableAccentColor // Ungu
        else -> CardBlue
    }
}