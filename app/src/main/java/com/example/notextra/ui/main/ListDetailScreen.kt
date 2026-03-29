package com.example.notextra.ui.main

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notextra.ui.components.AppBaseDialog
import com.example.notextra.ui.components.TableCellBody
import com.example.notextra.ui.components.TableCellCheckbox
import com.example.notextra.ui.components.TableCellDropdown
import com.example.notextra.ui.components.TableCellHeader
import com.example.notextra.ui.theme.AppDeleteColor
import com.example.notextra.ui.theme.AppPrimaryColor
import com.example.notextra.ui.theme.BgColor
import com.example.notextra.ui.theme.FabColor
import com.example.notextra.ui.theme.TextDark
import com.example.notextra.ui.theme.TextGray
import com.example.notextra.utils.DateUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    noteId: Int,
    viewModel: NoteViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    val listNotes by viewModel.listNotes.collectAsState()
    val note = listNotes.find { it.id == noteId }

    val listItemsFlow = remember(noteId) { viewModel.getListItems(noteId) }
    val listItems by listItemsFlow.collectAsState()

    var hasChanges by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showBackDialog by remember { mutableStateOf(false) }

    var showAddDataDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // State Sorting & Filtering
    var showSortMenu by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf("Default") }
    var showFilterMenu by remember { mutableStateOf(false) }
    var filterOption by remember { mutableStateOf("Default") }

// Logika Pintar: Search -> Filter -> Sort
    val displayedItems = remember(listItems, sortOption, filterOption, searchQuery) {
        var result = listItems

        // 1. Eksekusi Pencarian (Search) di 3 kolom sekaligus
        if (searchQuery.isNotBlank()) {
            val query = searchQuery.lowercase()
            result = result.filter {
                it.nama.lowercase().contains(query) ||
                        it.catatan1.lowercase().contains(query) ||
                        it.catatan2.lowercase().contains(query)
            }
        }

        // 2. Eksekusi Filter Status
        result = when (filterOption) {
            "Belum" -> result.filter { it.status == "Belum" }
            "Selesai" -> result.filter { it.status == "Selesai" }
            "Proses" -> result.filter { it.status == "Proses" }
            "Ditunda" -> result.filter { it.status == "Ditunda" }
            else -> result
        }

        // 3. Eksekusi Sorting
        when (sortOption) {
            "A-Z" -> result.sortedBy { it.nama.lowercase() }
            "Z-A" -> result.sortedByDescending { it.nama.lowercase() }
            else -> result
        }
    }

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

    Scaffold(
        topBar = {
            // TAHAP 1: TOPBAR SAMA DENGAN EDIT NOTE
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
        containerColor = BgColor // Background abu-abu sangat terang
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // --- TAHAP 2 & 5: HEADER JUDUL & TOMBOL ADD DI ATAS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sisi Kiri: Judul dan Tanggal yang bersih tanpa kotak
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = note?.title ?: "Judul List",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${note?.let { DateUtils.formatTimestamp(it.timestamp) }}  •  List Mode",
                        fontSize = 14.sp,
                        color = TextGray
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Sisi Kanan: Tombol Add Data yang Elegan
                Button(
                    onClick = { showAddDataDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = FabColor),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            androidx.compose.material3.HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 24.dp))
            Spacer(modifier = Modifier.height(16.dp))

            // --- TAHAP 3 & 4: WORKSPACE TABEL ---
            // Menggunakan background putih untuk area kerja
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // BARIS KONTROL: Search, Filter, Sort (Desain Baru Clean)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Kotak Search
// Kotak Search (Sekarang bisa diketik)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        androidx.compose.foundation.text.BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, color = TextDark),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Search, contentDescription = "Search", tint = TextGray, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    // Placeholder muncul kalau kosong
                                    Box(modifier = Modifier.weight(1f)) {
                                        if (searchQuery.isEmpty()) {
                                            Text("Search...", color = TextGray, fontSize = 13.sp)
                                        }
                                        innerTextField()
                                    }
                                    // Tombol (X) untuk clear text jika ada isinya
                                    if (searchQuery.isNotEmpty()) {
                                        Icon(
                                            imageVector = androidx.compose.material.icons.Icons.Default.Clear,
                                            contentDescription = "Clear",
                                            tint = TextGray,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable { searchQuery = "" }
                                        )
                                    }
                                }
                            }
                        )
                    }

                    // Tombol Filter
                    Box {
                        val (filterBgColor, filterBorderColor, filterTextColor) = when (filterOption) {
                            "Selesai" -> Triple(Color(0xFFDCFCE7), Color(0xFF4CAF50), Color(0xFF166534)) // Hijau
                            "Proses" -> Triple(Color(0xFFDBEAFE), Color(0xFF3B82F6), Color(0xFF1E40AF))  // Biru
                            "Belum" -> Triple(Color(0xFFFEF3C7), Color(0xFFF59E0B), Color(0xFF92400E))   // Oranye/Kuning
                            "Ditunda" -> Triple(Color(0xFFFEE2E2), Color(0xFFEF4444), Color(0xFF991B1B)) // Merah
                            else -> Triple(Color.White, Color(0xFFE5E7EB), TextDark) // Default (Putih Abu)
                        }

                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .background(filterBgColor, RoundedCornerShape(8.dp))
                                .border(1.dp, filterBorderColor, RoundedCornerShape(8.dp))
                                .clickable { showFilterMenu = true }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Filter", tint = filterTextColor, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (filterOption == "Default") "Filter" else filterOption,
                                    color = filterTextColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        DropdownMenu(expanded = showFilterMenu, onDismissRequest = { showFilterMenu = false }, modifier = Modifier.background(Color.White)) {
                            listOf("Default", "Belum", "Selesai", "Proses", "Ditunda").forEach { opt ->
                                DropdownMenuItem(text = { Text(opt, fontSize = 13.sp, color = TextDark) }, onClick = { filterOption = opt; showFilterMenu = false })
                            }
                        }
                    }

                    // Tombol Sort
                    Box {
                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
                                .clickable { showSortMenu = true }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Sort", tint = TextDark, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (sortOption == "Default") "Sort" else sortOption, color = TextDark, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                        DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }, modifier = Modifier.background(Color.White)) {
                            listOf("Default", "A-Z", "Z-A").forEach { opt ->
                                DropdownMenuItem(text = { Text(opt, fontSize = 13.sp, color = TextDark) }, onClick = { sortOption = opt; showSortMenu = false })
                            }
                        }
                    }
                }

                // TABEL (Struktur & Logika 100% sama, hanya ganti style komponen sel di bawah)
                val columnWidths = listOf(64.dp, 220.dp, 100.dp, 220.dp, 220.dp, 60.dp)

                // Membungkus tabel dengan Card putih bergaya clean
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        // Header Tabel
                        Row(modifier = Modifier.height(IntrinsicSize.Max)) {
                            TableCellHeader("#", columnWidths[0])
                            TableCellHeader("Nama", columnWidths[1])
                            TableCellHeader("Status", columnWidths[2])
                            TableCellHeader("Catatan 1", columnWidths[3])
                            TableCellHeader("Catatan 2", columnWidths[4])
                            TableCellHeader("Check", columnWidths[5])
                        }

                        // Logika Render Baris Tabel (SAMA PERSIS)
                        val displayRowCount = if (filterOption == "Default") max(3, displayedItems.size) else displayedItems.size

                        for (i in 0 until displayRowCount) {
                            Row(modifier = Modifier.height(IntrinsicSize.Max)) {
                                if (i < displayedItems.size) {
                                    val item = displayedItems[i]
                                    TableCellBody(item.sequenceNumber.toString(), columnWidths[0], isCenter = true)
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
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // --- POPUP DIALOGS (SAMA PERSIS) ---
    if (showAddDataDialog) {
        var nama by remember { mutableStateOf("") }
        var catatan1 by remember { mutableStateOf("") }
        var catatan2 by remember { mutableStateOf("") }

        val isNamaError = nama.length > 150
        val isCat1Error = catatan1.length > 150
        val isCat2Error = catatan2.length > 150
        val isFormValid = !isNamaError && !isCat1Error && !isCat2Error && nama.isNotBlank()

        AppBaseDialog(
            title = "Insert Data List", confirmText = "Tambah", dismissText = "Batal", isConfirmEnabled = isFormValid,
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
                OutlinedTextField(value = nama, onValueChange = { nama = it }, label = { Text("Nama") }, singleLine = true, isError = isNamaError, modifier = Modifier.fillMaxWidth(), supportingText = { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { if (isNamaError) Text("Melebihi batas!", color = Color.Red) else Spacer(Modifier.width(1.dp)); Text("${nama.length}/150") } })
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(value = catatan1, onValueChange = { catatan1 = it }, label = { Text("Catatan 1") }, singleLine = true, isError = isCat1Error, modifier = Modifier.fillMaxWidth(), supportingText = { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { if (isCat1Error) Text("Melebihi batas!", color = Color.Red) else Spacer(Modifier.width(1.dp)); Text("${catatan1.length}/150") } })
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(value = catatan2, onValueChange = { catatan2 = it }, label = { Text("Catatan 2") }, minLines = 2, isError = isCat2Error, modifier = Modifier.fillMaxWidth(), supportingText = { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { if (isCat2Error) Text("Melebihi batas!", color = Color.Red) else Spacer(Modifier.width(1.dp)); Text("${catatan2.length}/150") } })
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
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.padding(bottom = 16.dp).size(48.dp), tint = AppPrimaryColor)
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
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.padding(bottom = 16.dp).size(48.dp), tint = AppDeleteColor)
                Text("Perubahan belum disimpan, apa yang ingin Anda lakukan?", textAlign = TextAlign.Center)
            }
        }
    }
}