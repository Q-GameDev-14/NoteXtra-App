package com.example.notextra.ui.main

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notextra.domain.model.Note
import com.example.notextra.ui.components.AppBaseDialog
import com.example.notextra.ui.components.AllNoteCardItem
import com.example.notextra.ui.components.CreateListDialog
import com.example.notextra.ui.components.CreateNoteDialog
import com.example.notextra.ui.components.PinnedNoteCardMockup
import com.example.notextra.ui.components.QuickEditNoteDialog
import com.example.notextra.ui.components.getCategoryColor
import com.example.notextra.ui.theme.*

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
        0 -> Color(0xFFFFF1F2)
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
                                        text = "Show More >>", color = FabColor, fontSize = 13.sp, fontWeight = FontWeight.Bold,
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
            onCreate = { tipe, judul, kategori, konfigurasiKolom ->
                if (tipe == "Default") {
                    viewModel.addNote(judul, content = "", noteType = "LIST", category = kategori)
                    showCreateListModal = false
                } else {
                    // TODO: Logic Custom Table (JSON) akan kita buat di next step!
                    Toast.makeText(context, "Menerima ${konfigurasiKolom.size} kolom!", Toast.LENGTH_SHORT).show()
                    showCreateListModal = false
                }
            }
        )
    }

    if (noteToDelete != null) {
        AppBaseDialog(
            title = "Konfirmasi Hapus", confirmText = "Delete", dismissText = "Cancel",
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