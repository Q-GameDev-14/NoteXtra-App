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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
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
import androidx.compose.material3.HorizontalDivider
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

/**[MainScreen] adalah layar utama aplikasi yang memuat navigasi antar Tab (Notes, List, Settings).*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: NoteViewModel,
    onNavigateToEdit: (Int) -> Unit,
    onNavigateToListDetail: (Int) -> Unit
) {
    val context = LocalContext.current
    // ==========================================
    // 1. STATE & DATA MANAGEMENT
    // ==========================================
    // Mengambil data terkini dari Database (Otomatis update jika ada perubahan)
    val notes by viewModel.notes.collectAsState()
    val listNotes by viewModel.listNotes.collectAsState()

    // State untuk mengetahui Tab mana yang sedang aktif (0: Notes, 1: List, 2: Settings)
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    // State untuk menyimpan Note yang sedang dipilih (untuk Edit Cepat atau Hapus)
    var noteToDelete by remember { mutableStateOf<Note?>(null) }
    var noteToQuickEdit by remember { mutableStateOf<Note?>(null) }

    // State untuk mengontrol kemunculan Pop-up Dialog
    var showAddDialog by remember { mutableStateOf(false) }
    var showCreateListModal by remember { mutableStateOf(false) }

    // Menentukan background dinamis berdasarkan Tab yang aktif
    val currentBgColor = when (selectedTab) {
        0 -> Color(0xFFC4D8E7)
        1 -> Color(0xFFF0F9FF)
        else -> Color(0xFFF9FAFB)
    }

    // ==========================================
    // 2. KERANGKA LAYAR (SCAFFOLD)
    // ==========================================
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Note Xtra", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = TextDark) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = currentBgColor), // Transparan menyatu background
                actions = {
                    // --- MENU PENCARIAN & MORE (TITIK TIGA) ---
                    var showMoreMenu by remember { mutableStateOf(false) }

                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = TextDark, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    Box {
                        Box(
                            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray.copy(alpha = 0.3f)).clickable { showMoreMenu = true },
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Default.MoreVert, contentDescription = "More", tint = TextDark, modifier = Modifier.size(24.dp)) }

                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false },
                            modifier = Modifier.background(Color.White).width(200.dp)
                        ) {
                            Text("SORT", color = TextGray, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                            DropdownMenuItem(text = { Text("Title A - Z", color = TextDark) }, onClick = { showMoreMenu = false })
                            DropdownMenuItem(text = { Text("Title Z - A", color = TextDark) }, onClick = { showMoreMenu = false })

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.LightGray.copy(alpha = 0.3f))

                            Text("FILTER", color = TextGray, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                            val categories = listOf("Work", "Personal", "Idea", "Organization")
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(getCategoryColor(cat)))
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(cat, color = TextDark)
                                        }
                                    },
                                    onClick = { showMoreMenu = false }
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.LightGray.copy(alpha = 0.3f))

                            Text("MANAGE", color = TextGray, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                            DropdownMenuItem(text = { Text("Select All", color = TextDark) }, onClick = { showMoreMenu = false })
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
            )
        },
        bottomBar = {
            // --- NAVIGASI BAWAH (BOTTOM BAR) ---
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 16.dp,
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0, onClick = { selectedTab = 0 },
                    icon = { Icon(androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_edit), contentDescription = "Notes", modifier = Modifier.size(24.dp)) },
                    label = { Text("Notes", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = FabColor, selectedTextColor = FabColor, indicatorColor = Color.Transparent, unselectedIconColor = TextGray, unselectedTextColor = TextGray)
                )
                NavigationBarItem(
                    selected = selectedTab == 1, onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.List, contentDescription = "List") },
                    label = { Text("List", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = FabColor, selectedTextColor = FabColor, indicatorColor = Color.Transparent, unselectedIconColor = TextGray, unselectedTextColor = TextGray)
                )
                NavigationBarItem(
                    selected = selectedTab == 2, onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = FabColor, selectedTextColor = FabColor, indicatorColor = Color.Transparent, unselectedIconColor = TextGray, unselectedTextColor = TextGray)
                )
            }
        },
        floatingActionButton = {
            // --- TOMBOL TAMBAH (+) ---
            if (selectedTab != 2) {
                FloatingActionButton(
                    onClick = { if (selectedTab == 0) showAddDialog = true else if (selectedTab == 1) showCreateListModal = true },
                    containerColor = FabColor,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(32.dp))
                }
            }
        },
        containerColor = currentBgColor
    ) { paddingValues ->

        // ==========================================
        // 3. KONTEN UTAMA BERDASARKAN TAB
        // ==========================================
        when (selectedTab) {
            // ------------------------------------------
            // TAB 0: LAYAR DAFTAR CATATAN (NOTES)
            // ------------------------------------------
            0 -> {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    val pinnedNotes = notes.filter { it.isPinned }
                    val unpinnedNotes = notes.filter { !it.isPinned }

                    // A. Bagian Pinned Notes (Grid 2x2)
                    if (pinnedNotes.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 12.dp)) {
                                    Text("📌", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("PINNED", color = TextGray, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.5.sp)
                                }

                                val chunkedPinned = pinnedNotes.take(4).chunked(2)
                                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                                    chunkedPinned.forEach { rowItems ->
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                            rowItems.forEach { note ->
                                                PinnedNoteCardMockup(
                                                    modifier = Modifier.weight(1f),
                                                    note = note,
                                                    bgColor = getCategoryColor(note.category),
                                                    onClick = { onNavigateToEdit(note.id) },
                                                    onLongClick = { noteToQuickEdit = note }
                                                )
                                            }
                                            if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
                                        }
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                }

                                if (pinnedNotes.size > 4) {
                                    Text(
                                        text = "Tampilkan lebih banyak >>", color = FabColor, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                        modifier = Modifier.fillMaxWidth().clickable { /* TODO: Arahkan ke screen baru nanti */ }.padding(vertical = 8.dp), textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // B. Bagian Semua Catatan Biasa (Swipe to Delete)
                    item { Text("ALL NOTES", color = TextGray, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.5.sp, modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 8.dp)) }

                    items(items = unpinnedNotes, key = { it.id }) { note ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { if (it == SwipeToDismissBoxValue.EndToStart) { noteToDelete = note; false } else false }
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
                                    note = note, barColor = getCategoryColor(note.category),
                                    onClick = { onNavigateToEdit(note.id) },
                                    onLongClick = { noteToQuickEdit = note }
                                )
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }

            // ------------------------------------------
            // TAB 1: LAYAR DAFTAR TABEL (LIST)
            // ------------------------------------------
            1 -> {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    val pinnedLists = listNotes.filter { it.isPinned }
                    val unpinnedLists = listNotes.filter { !it.isPinned }

                    // A. Bagian Pinned Lists
                    if (pinnedLists.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 12.dp)) {
                                    Text("📌", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("PINNED", color = TextGray, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.5.sp)
                                }

                                val chunkedPinned = pinnedLists.take(4).chunked(2)
                                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                                    chunkedPinned.forEach { rowItems ->
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                            rowItems.forEach { note ->
                                                PinnedNoteCardMockup(
                                                    modifier = Modifier.weight(1f),
                                                    note = note,
                                                    bgColor = getCategoryColor(note.category),
                                                    onClick = { onNavigateToListDetail(note.id) },
                                                    onLongClick = { noteToQuickEdit = note }
                                                )
                                            }
                                            if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
                                        }
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                }

                                if (pinnedLists.size > 4) {
                                    Text(
                                        text = "Tampilkan lebih banyak >>", color = FabColor, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                        modifier = Modifier.fillMaxWidth().clickable { /* TODO: Arahkan ke screen baru nanti */ }.padding(vertical = 8.dp), textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // B. Bagian Semua List
                    item { Text("ALL LIST", color = TextGray, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.5.sp, modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 8.dp)) }

                    items(items = unpinnedLists, key = { it.id }) { note ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { if (it == SwipeToDismissBoxValue.EndToStart) { noteToDelete = note; false } else false }
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
                                    note = note, barColor = getCategoryColor(note.category),
                                    onClick = { onNavigateToListDetail(note.id) },
                                    onLongClick = { noteToQuickEdit = note }
                                )
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
            // ------------------------------------------
            // TAB 2: SETTINGS (Belum Diimplementasikan)
            // ------------------------------------------
            2 -> { Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) }
        }
    }

    // ==========================================
    // 4. KUMPULAN DIALOG (POP-UP)
    // ==========================================
    if (showAddDialog) {
        CreateNoteDialog(
            onDismiss = { showAddDialog = false },
            onCreate = { judul, isi, kategori ->
                viewModel.addNote(title = judul, content = isi, noteType = "REGULAR", category = kategori)
                showAddDialog = false
            }
        )
    }

    if (showCreateListModal) {
        CreateListDialog(
            onDismiss = { showCreateListModal = false },
            onCreate = { tipe, judul, kategori ->
                if (tipe == "Default") {
                    viewModel.addNote(judul, content = "", noteType = "LIST", category = kategori)
                    showCreateListModal = false
                } else {
                    Toast.makeText(context, "Custom Table belum tersedia", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    if (noteToDelete != null) {
        AppBaseDialog(
            title = "Konfirmasi Hapus", confirmText = "Hapus", dismissText = "Batal",
            onDismissRequest = { noteToDelete = null },
            onConfirm = { noteToDelete?.let { viewModel.deleteNote(it) }; noteToDelete = null }
        ) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.padding(bottom = 16.dp).size(56.dp), tint = AppDeleteColor)
                Text(text = "Apakah Anda yakin ingin menghapus '${noteToDelete?.title}'?", color = Color.Black, textAlign = TextAlign.Center)
            }
        }
    }

    if (noteToQuickEdit != null) {
        QuickEditNoteDialog(
            note = noteToQuickEdit!!,
            onDismiss = { noteToQuickEdit = null },
            onSave = { judulBaru, kategoriBaru, isPinnedBaru ->
                viewModel.updateNote(note = noteToQuickEdit!!, newTitle = judulBaru, newContent = noteToQuickEdit!!.content, newCategory = kategoriBaru, newIsPinned = isPinnedBaru)
                noteToQuickEdit = null
            }
        )
    }
}

// =======================================================
// SUB-KOMPONEN UI PENDUKUNG (CETAKAN UI)
// =======================================================
/** Komponen kotak untuk daftar Pinned Notes di bagian atas layar */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PinnedNoteCardMockup(modifier: Modifier = Modifier, note: Note, bgColor: Color, onClick: () -> Unit, onLongClick: () -> Unit = {}) {
    Box(
        modifier = modifier.height(110.dp).clip(RoundedCornerShape(16.dp)).background(bgColor).combinedClickable(onClick = onClick, onLongClick = onLongClick).padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Text(text = note.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(text = "${DateUtils.formatTimestamp(note.timestamp)} · ${note.category}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
        }
    }
}

/** Komponen baris memanjang untuk daftar semua Notes (All Notes) */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllNoteCardItem(note: Note, barColor: Color, onClick: () -> Unit, onLongClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp).combinedClickable(onClick = onClick, onLongClick = onLongClick).shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.width(4.dp).height(32.dp).clip(RoundedCornerShape(4.dp)).background(barColor)) // Garis vertikal warna
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = note.title, color = TextDark, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${DateUtils.formatTimestamp(note.timestamp)} · ${note.category}", color = TextGray, fontSize = 12.sp)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Open", tint = Color.LightGray, modifier = Modifier.size(20.dp))
        }
    }
}

/** Dialog untuk mengedit nama, kategori, dan status pin secara cepat */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickEditNoteDialog(note: Note, onDismiss: () -> Unit, onSave: (String, String, Boolean) -> Unit) {
    var judul by remember { mutableStateOf(note.title) }
    var kategori by remember { mutableStateOf(note.category) }
    var isPinned by remember { mutableStateOf(note.isPinned) }
    var expandedKategori by remember { mutableStateOf(false) }
    val opsiKategori = listOf("Work", "Personal", "Organization", "Idea")

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE5E7EB))) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = note.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), maxLines = 2, overflow = TextOverflow.Ellipsis)

                Text("Judul Note", color = TextDark, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp, start = 4.dp))
                OutlinedTextField(value = judul, onValueChange = { judul = it }, maxLines = 2, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = outlinedTextFieldColors(focusedBorderColor = TextDark, unfocusedBorderColor = Color.Gray, containerColor = Color.Transparent))
                Spacer(modifier = Modifier.height(16.dp))

                Text("Kategori", color = TextDark, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp, start = 4.dp))
                Box {
                    OutlinedTextField(value = kategori, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Dropdown", modifier = Modifier.clickable { expandedKategori = true }) }, colors = outlinedTextFieldColors(focusedBorderColor = TextDark, unfocusedBorderColor = Color.Gray, containerColor = Color.Transparent))
                    DropdownMenu(expanded = expandedKategori, onDismissRequest = { expandedKategori = false }) {
                        opsiKategori.forEach { opt -> DropdownMenuItem(text = { Text(opt) }, onClick = { kategori = opt; expandedKategori = false }) }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(onClick = { isPinned = !isPinned }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color.Gray), colors = ButtonDefaults.outlinedButtonColors(containerColor = if (isPinned) TableAccentColor.copy(alpha = 0.2f) else Color.Transparent)) {
                    Text(if (isPinned) "Ter-Pinned 📌" else "Pinned", color = TextDark)
                }
                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = onDismiss, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7986CB))) { Text("Batal") }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { onSave(judul, kategori, isPinned) }, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7986CB))) { Text("Simpan") }
                }
            }
        }
    }
}

/** Utility pembantu untuk menerjemahkan nama Kategori teks menjadi Warna UI */
fun getCategoryColor(category: String): Color {
    return when(category) {
        "Work" -> CardBlue
        "Personal" -> CardGreen
        "Organization" -> CardOrange
        "Idea" -> TableAccentColor
        else -> CardBlue
    }
}

/** Dialog interaktif 2-Step saat membuat List Baru */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListDialog(onDismiss: () -> Unit, onCreate: (String, String, String) -> Unit) {
    var step by remember { mutableIntStateOf(1) }
    var selectedType by remember { mutableStateOf("Default") }
    var listTitle by remember { mutableStateOf("") }
    var listCategory by remember { mutableStateOf("Work") }
    var expandedCategory by remember { mutableStateOf(false) }
    val kategoriOptions = listOf("Work", "Personal", "Organization", "Idea")

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.width(40.dp).height(4.dp).clip(CircleShape).background(Color.LightGray.copy(alpha = 0.5f)))
                Spacer(modifier = Modifier.height(16.dp))

                if (step == 1) {
                    Text("Tambah List", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text("Step 1 of 2 — choose type", fontSize = 14.sp, color = FabColor)
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        TypeSelectionCard(modifier = Modifier.weight(1f), title = "Default", subtitle = "Ready-made", icon = Icons.Default.List, isSelected = selectedType == "Default", onClick = { selectedType = "Default" })
                        TypeSelectionCard(modifier = Modifier.weight(1f), title = "Custom", subtitle = "Define columns", icon = Icons.Default.Build, isSelected = selectedType == "Custom", onClick = { selectedType = "Custom" })
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth().height(4.dp)) {
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FabColor, RoundedCornerShape(2.dp)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(2.dp)))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { step = 2 }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = FabColor), shape = RoundedCornerShape(12.dp)) { Text("Continue →", fontWeight = FontWeight.Bold) }
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.TextButton(onClick = onDismiss) { Text("Cancel", color = FabColor, fontWeight = FontWeight.Bold) }
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Back", tint = TextGray, modifier = Modifier.clickable { step = 1 })
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Back to type", color = TextGray, fontSize = 14.sp, modifier = Modifier.clickable { step = 1 })
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Name your list", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.fillMaxWidth())
                    Text("Step 2 of 2 — $selectedType Table", fontSize = 14.sp, color = FabColor, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(value = listTitle, onValueChange = { listTitle = it }, label = { Text("List title") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = outlinedTextFieldColors(focusedBorderColor = FabColor, unfocusedBorderColor = FabColor))
                    Spacer(modifier = Modifier.height(24.dp))
                    Box {
                        OutlinedTextField(value = listCategory, onValueChange = {}, readOnly = true, label = { Text("Category") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Dropdown", modifier = Modifier.clickable { expandedCategory = true }) }, colors = outlinedTextFieldColors(focusedBorderColor = FabColor, unfocusedBorderColor = FabColor))
                        DropdownMenu(expanded = expandedCategory, onDismissRequest = { expandedCategory = false }) {
                            kategoriOptions.forEach { opt -> DropdownMenuItem(text = { Row(verticalAlignment = Alignment.CenterVertically) { Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(getCategoryColor(opt))); Spacer(modifier = Modifier.width(12.dp)); Text(opt, color = TextDark) } }, onClick = { listCategory = opt; expandedCategory = false }) }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth().height(4.dp)) {
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FabColor, RoundedCornerShape(2.dp)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(FabColor, RoundedCornerShape(2.dp)))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { if (listTitle.isNotBlank()) onCreate(selectedType, listTitle, listCategory) }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = FabColor), shape = RoundedCornerShape(12.dp)) { Text("Buat List", fontWeight = FontWeight.Bold) }
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.TextButton(onClick = onDismiss) { Text("Cancel", color = FabColor, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

/** Card pembantu untuk dialog Create List */
@Composable
fun TypeSelectionCard(modifier: Modifier = Modifier, title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) FabColor else Color.LightGray.copy(alpha = 0.5f)
    val bgColor = if (isSelected) Color(0xFFF0F4FF) else Color.Transparent
    Card(modifier = modifier.height(130.dp).clickable { onClick() }, shape = RoundedCornerShape(16.dp), border = BorderStroke(2.dp, borderColor), colors = CardDefaults.cardColors(containerColor = bgColor)) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFE0E7FF)), contentAlignment = Alignment.Center) { Icon(icon, contentDescription = null, tint = FabColor, modifier = Modifier.size(24.dp)) }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, color = TextDark)
            Text(subtitle, fontSize = 11.sp, color = TextGray)
        }
    }
}

/** Dialog interaktif saat membuat Note Baru */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoteDialog(onDismiss: () -> Unit, onCreate: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Work") }
    var expandedCategory by remember { mutableStateOf(false) }
    val kategoriOptions = listOf("Work", "Personal", "Organization", "Idea")

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("New note", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Fill in the details below", fontSize = 14.sp, color = FabColor, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("TITLE", fontSize = 12.sp, fontWeight = FontWeight.Bold) }, modifier = Modifier.fillMaxWidth(), maxLines = 2, shape = RoundedCornerShape(12.dp), colors = outlinedTextFieldColors(focusedBorderColor = FabColor, unfocusedBorderColor = Color.LightGray))
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("CONTENT", fontSize = 12.sp, fontWeight = FontWeight.Bold) }, modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp), minLines = 3, shape = RoundedCornerShape(12.dp), colors = outlinedTextFieldColors(focusedBorderColor = FabColor, unfocusedBorderColor = Color.LightGray))
                Spacer(modifier = Modifier.height(16.dp))

                Box {
                    OutlinedTextField(value = category, onValueChange = {}, readOnly = true, label = { Text("CATEGORY", fontSize = 12.sp, fontWeight = FontWeight.Bold) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Dropdown", modifier = Modifier.clickable { expandedCategory = true }) }, colors = outlinedTextFieldColors(focusedBorderColor = FabColor, unfocusedBorderColor = Color.LightGray))
                    DropdownMenu(expanded = expandedCategory, onDismissRequest = { expandedCategory = false }) {
                        kategoriOptions.forEach { opt -> DropdownMenuItem(text = { Row(verticalAlignment = Alignment.CenterVertically) { Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(getCategoryColor(opt))); Spacer(modifier = Modifier.width(12.dp)); Text(opt, color = TextDark) } }, onClick = { category = opt; expandedCategory = false }) }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color.LightGray)) { Text("Batal", color = FabColor, fontWeight = FontWeight.Bold) }
                    Button(onClick = { if (title.isNotBlank()) onCreate(title, content, category) }, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = FabColor)) { Text("Simpan", color = Color.White, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}