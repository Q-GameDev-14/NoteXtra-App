package com.example.notextra.ui.main

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notextra.ui.components.AppBaseDialog
import com.example.notextra.ui.theme.AppBackgroundColor
import com.example.notextra.ui.theme.AppDeleteColor
import com.example.notextra.ui.theme.AppHeaderColor
import com.example.notextra.ui.theme.AppPrimaryColor
import com.example.notextra.ui.theme.AppSecondaryColor
import com.example.notextra.ui.theme.StatusBelumColor
import com.example.notextra.ui.theme.StatusDitundaColor
import com.example.notextra.ui.theme.StatusInProgressColor
import com.example.notextra.ui.theme.StatusSelesaiColor
import com.example.notextra.ui.theme.TableAccentColor
import com.example.notextra.ui.theme.TableHeaderColor
import com.example.notextra.utils.DateUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

/**[ListDetailScreen] adalah layar yang menampilkan detail dari LIST.*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    noteId: Int,
    viewModel: NoteViewModel,
    onNavigateBack: () -> Unit
) {
    // ==========================================
    // 1. INISIALISASI DATA & UTILITY
    // ==========================================
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    // Mengambil data Induk (Note) dan Anak-anaknya (ListItem)
    val listNotes by viewModel.listNotes.collectAsState()
    val note = listNotes.find { it.id == noteId }
    val listItemsFlow = remember(noteId) { viewModel.getListItems(noteId) }
    val listItems by listItemsFlow.collectAsState()

    // ==========================================
    // 2. STATE MANAGEMENT (Variabel Pengontrol Layar)
    // ==========================================
    // State Penyimpanan & Navigasi
    var hasChanges by remember { mutableStateOf(false) } // Menandai jika ada item yang diubah
    var showSaveDialog by remember { mutableStateOf(false) }
    var showBackDialog by remember { mutableStateOf(false) }

    // State Kontrol Tabel
    var isViewMode by remember { mutableStateOf(true) } // Toggle Mode View/Edit (UI Only)
    var showAddDataDialog by remember { mutableStateOf(false) }

    // State Sorting & Filtering
    var showSortMenu by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf("Default") }
    var showFilterMenu by remember { mutableStateOf(false) }
    var filterOption by remember { mutableStateOf("Default") }

    // ==========================================
    // 3. LOGIKA BISNIS (Sorting & Filtering)
    // ==========================================
    val displayedItems = remember(listItems, sortOption, filterOption) {
        val filtered = when (filterOption) {
            "Belum" -> listItems.filter { it.status == "Belum" }
            "Selesai" -> listItems.filter { it.status == "Selesai" }
            "In Progress" -> listItems.filter { it.status == "In Progress" }
            "Ditunda" -> listItems.filter { it.status == "Ditunda" }
            else -> listItems
        }
        when (sortOption) {
            "A-Z" -> filtered.sortedBy { it.nama.lowercase() }
            "Z-A" -> filtered.sortedByDescending { it.nama.lowercase() }
            else -> filtered
        }
    }

    // ==========================================
    // 4. FUNGSI AKSI UTAMA SIMPAN
    // ==========================================
    val performSave = {
        focusManager.clearFocus()
        if (hasChanges && note != null) {
            viewModel.updateNote(note, note.title, note.content)
        }
        Toast.makeText(context, "Perubahan berhasil disimpan", Toast.LENGTH_SHORT).show()
        onNavigateBack()
    }

    BackHandler {
        if (hasChanges) showBackDialog = true else onNavigateBack()
    }

    // ==========================================
    // 5. UI UTAMA (SCAFFOLD)
    // ==========================================
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Note Xtra", color = Color.White) },
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
            // --- SECTION 1: HEADER CARD (Judul & Tanggal) ---
            Card(
                colors = CardDefaults.cardColors(containerColor = AppSecondaryColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = note?.title ?: "Judul List", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Normal)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = note?.let { DateUtils.formatTimestamp(it.timestamp) } ?: "tanggal", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { isViewMode = !isViewMode },
                        colors = ButtonDefaults.buttonColors(containerColor = TableAccentColor),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Mode", tint = Color.White, modifier = Modifier.height(20.dp).width(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isViewMode) "View" else "Edit", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- SECTION 2: WORKSPACE (Tabel & Kontrol) ---
            Card(
                colors = CardDefaults.cardColors(containerColor = AppPrimaryColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Button(
                        onClick = { showAddDataDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = TableAccentColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("+ Add Data", fontWeight = FontWeight.Bold) }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Baris Kontrol: Search, Filter, Sort
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f).height(36.dp).background(TableAccentColor.copy(alpha = 0.8f), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp), contentAlignment = Alignment.CenterStart) {
                            Text("Search", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        Box {
                            val filterButtonColor = when (filterOption) {
                                "Selesai" -> StatusSelesaiColor
                                "In Progress" -> StatusInProgressColor
                                "Ditunda" -> StatusDitundaColor
                                "Belum" -> StatusBelumColor
                                else -> TableAccentColor.copy(alpha = 0.8f)
                            }
                            Button(
                                onClick = { showFilterMenu = true },
                                colors = ButtonDefaults.buttonColors(containerColor = filterButtonColor),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.Menu, contentDescription = "Mode", tint = Color.White, modifier = Modifier.height(20.dp).width(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (filterOption == "Default") "Filter" else filterOption, fontSize = 11.sp)
                            }
                            DropdownMenu(expanded = showFilterMenu, onDismissRequest = { showFilterMenu = false }) {
                                listOf("Default", "Belum", "Selesai", "In Progress", "Ditunda").forEach { opt ->
                                    DropdownMenuItem(text = { Text(opt, fontSize = 12.sp) }, onClick = { filterOption = opt; showFilterMenu = false })
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        Box {
                            Button(
                                onClick = { showSortMenu = true },
                                colors = ButtonDefaults.buttonColors(containerColor = TableAccentColor.copy(alpha = 0.8f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.Menu, contentDescription = "Mode", tint = Color.White, modifier = Modifier.height(20.dp).width(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (sortOption == "Default") "Sort" else sortOption, fontSize = 11.sp)
                            }
                            DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                                listOf("Default", "A-Z", "Z-A").forEach { opt ->
                                    DropdownMenuItem(text = { Text(opt, fontSize = 12.sp) }, onClick = { sortOption = opt; showSortMenu = false })
                                }
                            }
                        }
                    }

                    // --- STRUKTUR TABEL ---
                    val columnWidths = listOf(40.dp, 220.dp, 100.dp, 220.dp, 220.dp, 60.dp)

                    Column(modifier = Modifier.fillMaxWidth().border(1.dp, Color.White).horizontalScroll(rememberScrollState())) {

                        // Header Tabel
                        Row(modifier = Modifier.background(TableHeaderColor).height(IntrinsicSize.Max)) {
                            TableCellHeader("No", columnWidths[0])
                            TableCellHeader("Nama", columnWidths[1])
                            TableCellHeader("Status", columnWidths[2])
                            TableCellHeader("Catatan 1", columnWidths[3])
                            TableCellHeader("Catatan 2", columnWidths[4])
                            TableCellHeader("Check", columnWidths[5])
                        }

                        // Render minimal 3 baris kosong jika data kurang dari 3 (hanya jika tidak ada filter)
                        val displayRowCount = if (filterOption == "Default") max(3, displayedItems.size) else displayedItems.size

                        for (i in 0 until displayRowCount) {
                            Row(modifier = Modifier.background(Color.White).height(IntrinsicSize.Max)) {
                                if (i < displayedItems.size) {
                                    // Render data
                                    val item = displayedItems[i]
                                    TableCellBody(item.sequenceNumber.toString(), columnWidths[0])
                                    TableCellBody(item.nama, columnWidths[1])
                                    TableCellDropdown(item.status, columnWidths[2]) { newStatus ->
                                        viewModel.updateListItem(item.copy(status = newStatus))
                                        hasChanges = true
                                    }
                                    TableCellBody(item.catatan1, columnWidths[3])
                                    TableCellBody(item.catatan2, columnWidths[4])
                                    TableCellCheckbox(item.isChecked, columnWidths[5]) { isChecked ->
                                        viewModel.updateListItem(item.copy(isChecked = isChecked))
                                        hasChanges = true
                                    }
                                } else {
                                    // Render baris kosong (Placeholder)
                                    TableCellBody("", columnWidths[0])
                                    TableCellBody("", columnWidths[1])
                                    TableCellDropdown("Belum", columnWidths[2]) { }
                                    TableCellBody("", columnWidths[3])
                                    TableCellBody("", columnWidths[4])
                                    TableCellCheckbox(false, columnWidths[5]) { }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }

    // ==========================================
    // 6. KUMPULAN POP-UP DIALOG
    // ==========================================
    if (showAddDataDialog) {
        var nama by remember { mutableStateOf("") }
        var catatan1 by remember { mutableStateOf("") }
        var catatan2 by remember { mutableStateOf("") }
        val isNamaError = nama.length > 150
        val isCat1Error = catatan1.length > 150
        val isCat2Error = catatan2.length > 150
        val isFormValid = !isNamaError && !isCat1Error && !isCat2Error && nama.isNotBlank()

        AppBaseDialog(
            title = "Insert Data List",
            confirmText = "Tambah",
            dismissText = "Batal",
            isConfirmEnabled = isFormValid,
            onDismissRequest = { showAddDataDialog = false },
            onConfirm = {
                if (nama.isNotBlank()) {
                    viewModel.addListItem(noteId, nama, catatan1, catatan2)
                    hasChanges = true
                    showAddDataDialog = false
                }
            }
        ) {
            Column {
                OutlinedTextField(
                    value = nama, onValueChange = { nama = it }, label = { Text("Nama") }, singleLine = true, isError = isNamaError, modifier = Modifier.fillMaxWidth(),
                    supportingText = { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { if (isNamaError) Text("Melebihi batas!", color = Color.Red) else Spacer(Modifier.width(1.dp)); Text("${nama.length}/150") } }
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = catatan1, onValueChange = { catatan1 = it }, label = { Text("Catatan 1") }, singleLine = true, isError = isCat1Error, modifier = Modifier.fillMaxWidth(),
                    supportingText = { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { if (isCat1Error) Text("Melebihi batas!", color = Color.Red) else Spacer(Modifier.width(1.dp)); Text("${catatan1.length}/150") } }
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = catatan2, onValueChange = { catatan2 = it }, label = { Text("Catatan 2") }, minLines = 2, isError = isCat2Error, modifier = Modifier.fillMaxWidth(),
                    supportingText = { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { if (isCat2Error) Text("Melebihi batas!", color = Color.Red) else Spacer(Modifier.width(1.dp)); Text("${catatan2.length}/150") } }
                )
            }
        }
    }

    if (showSaveDialog) {
        AppBaseDialog(
            title = "Simpan Perubahan", confirmText = "Simpan", dismissText = "Batal",
            onDismissRequest = { showSaveDialog = false },
            onConfirm = { showSaveDialog = false; scope.launch { delay(150); performSave() } }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.padding(bottom = 16.dp).height(48.dp).width(48.dp), tint = AppPrimaryColor)
                Text("Apakah Anda yakin ingin menyimpan perubahan?", textAlign = TextAlign.Center)
            }
        }
    }

    if (showBackDialog) {
        AppBaseDialog(
            title = "Perubahan Belum Disimpan", confirmText = "Simpan", dismissText = "Lanjut", discardText = "Buang",
            onDismissRequest = { showBackDialog = false },
            onConfirm = { showBackDialog = false; scope.launch { delay(150); performSave() } },
            onDiscard = { showBackDialog = false; onNavigateBack() }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.padding(bottom = 16.dp).height(48.dp).width(48.dp), tint = AppDeleteColor)
                Text("Perubahan belum disimpan, apa yang ingin Anda lakukan?", textAlign = TextAlign.Center)
            }
        }
    }
}

// ==========================================
// 7. KOMPONEN PEMBANTU (HELPER) UNTUK TABEL
// ==========================================
/** Desain untuk sel di baris paling atas tabel (Header) */
@Composable
fun TableCellHeader(text: String, width: Dp) {
    Box(
        modifier = Modifier.width(width).fillMaxHeight().heightIn(min = 56.dp).border(0.5.dp, Color.LightGray).padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
    }
}

/** Desain untuk sel teks biasa di dalam tabel */
@Composable
fun TableCellBody(text: String, width: Dp) {
    Box(
        modifier = Modifier.width(width).fillMaxHeight().heightIn(min = 56.dp).border(0.5.dp, Color.LightGray).padding(8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text, color = Color.Black, fontSize = 12.sp, maxLines = 5, overflow = TextOverflow.Ellipsis, lineHeight = 16.sp)
    }
}

/** Desain sel interaktif berupa Dropdown untuk mengubah status item */
@Composable
fun TableCellDropdown(status: String, width: Dp, onStatusChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Belum", "Selesai", "In Progress", "Ditunda")
    val badgeColor = when (status) {
        "Selesai" -> StatusSelesaiColor
        "In Progress" -> StatusInProgressColor
        "Ditunda" -> StatusDitundaColor
        else -> StatusBelumColor
    }

    Box(
        modifier = Modifier.width(width).fillMaxHeight().heightIn(min = 56.dp).border(0.5.dp, Color.LightGray).clickable { expanded = true }.padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.fillMaxSize().height(36.dp).background(badgeColor, RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
            Text(text = status, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option, fontSize = 12.sp) }, onClick = { onStatusChange(option); expanded = false })
            }
        }
    }
}

/** Desain sel interaktif berupa Kotak Centang (Checkbox) */
@Composable
fun TableCellCheckbox(isChecked: Boolean, width: Dp, onCheckedChange: (Boolean) -> Unit) {
    Box(modifier = Modifier.width(width).fillMaxHeight().heightIn(min = 56.dp).border(0.5.dp, Color.LightGray), contentAlignment = Alignment.Center) {
        Checkbox(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}