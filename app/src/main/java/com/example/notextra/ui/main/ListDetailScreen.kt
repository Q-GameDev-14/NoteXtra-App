package com.example.notextra.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.launch
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    noteId: Int,
    viewModel: NoteViewModel,
    onNavigateBack: () -> Unit
) {
    // Mengambil info List Induk
    val listNotes by viewModel.listNotes.collectAsState()
    val note = listNotes.find { it.id == noteId }

    // 1. Mengambil data Baris Tabel (List Items) secara reaktif
    val listItemsFlow = remember(noteId) { viewModel.getListItems(noteId) }
    val listItems by listItemsFlow.collectAsState()

    // 2. State untuk menampilkan Pop-up Dialog Insert Data
    var showAddDataDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    var hasChanges by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showBackDialog by remember { mutableStateOf(false) }

    // Fungsi utama untuk menyelesaikan perubahan dan kembali
    val performSave = {
        focusManager.clearFocus()
        android.widget.Toast.makeText(context, "Perubahan berhasil disimpan", android.widget.Toast.LENGTH_SHORT).show()
        onNavigateBack()
    }

    // Logika Mencegat Tombol Back Fisik HP
    androidx.activity.compose.BackHandler {
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
            // --- CARD 1: JUDUL LIST (DIKEMBALIKAN KE STATIS) ---
            Card(
                colors = CardDefaults.cardColors(containerColor = AppSecondaryColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = note?.title ?: "Judul List",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = note?.let { DateUtils.formatTimestamp(it.timestamp) } ?: "tanggal",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- CARD 2: TABEL SPREADSHEET ---
            Card(
                colors = CardDefaults.cardColors(containerColor = AppPrimaryColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {

                    // Tombol Add Data yang memicu Pop-up
                    Button(
                        onClick = { showAddDataDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = TableAccentColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("+ Add Data", fontWeight = FontWeight.Bold) }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Header Card (Search, Filter, Sort)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f).height(36.dp).background(TableAccentColor.copy(alpha = 0.8f), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp), contentAlignment = Alignment.CenterStart) {
                            Text("Search", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { android.widget.Toast.makeText(context, "Fitur Filter belum tersedia", android.widget.Toast.LENGTH_SHORT).show() },
                            colors = ButtonDefaults.buttonColors(containerColor = TableAccentColor.copy(alpha = 0.8f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) { Text("Filter", fontSize = 12.sp) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { /* TODO di Tahap 3 nanti */ },
                            colors = ButtonDefaults.buttonColors(containerColor = TableAccentColor.copy(alpha = 0.8f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) { Text("Sort", fontSize = 12.sp) }
                    }

                    // TABEL UTAMA
                    val columnWidths = listOf(40.dp, 100.dp, 100.dp, 120.dp, 120.dp, 60.dp)

                    Column(modifier = Modifier.fillMaxWidth().border(1.dp, Color.White).horizontalScroll(rememberScrollState())) {
                        // Header Tabel
                        Row(modifier = Modifier.background(TableHeaderColor)) {
                            TableCellHeader("No", columnWidths[0])
                            TableCellHeader("Nama", columnWidths[1])
                            TableCellHeader("Status", columnWidths[2])
                            TableCellHeader("Catatan 1", columnWidths[3])
                            TableCellHeader("Catatan 2", columnWidths[4])
                            TableCellHeader("Check", columnWidths[5])
                        }

                        // Isi Tabel
                        val displayRowCount = max(3, listItems.size)
                        for (i in 0 until displayRowCount) {
                            Row(modifier = Modifier.background(Color.White)) {
                                if (i < listItems.size) {
                                    val item = listItems[i]
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

    // 4. POP-UP DIALOG INSERT DATA
    if (showAddDataDialog) {
        var nama by remember { mutableStateOf("") }
        var catatan1 by remember { mutableStateOf("") }
        var catatan2 by remember { mutableStateOf("") }

        com.example.notextra.ui.components.AppBaseDialog(
            title = "Insert Data List",
            confirmText = "Tambah",
            dismissText = "Batal",
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
                OutlinedTextField(value = nama, onValueChange = { nama = it }, label = { Text("Nama") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = catatan1, onValueChange = { catatan1 = it }, label = { Text("Catatan 1") }, minLines = 3, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = catatan2, onValueChange = { catatan2 = it }, label = { Text("Catatan 2 (Khusus Link)") }, minLines = 3, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    // --- DIALOG SIMPAN PERUBAHAN ---
    if (showSaveDialog) {
        com.example.notextra.ui.components.AppBaseDialog(
            title = "Simpan Perubahan",
            confirmText = "Simpan",
            dismissText = "Batal",
            onDismissRequest = { showSaveDialog = false },
            onConfirm = {
                showSaveDialog = false
                scope.launch {
                    kotlinx.coroutines.delay(150)
                    performSave()
                }
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.padding(bottom = 16.dp).height(48.dp).width(48.dp),
                    tint = AppPrimaryColor
                )
                Text("Apakah Anda yakin ingin menyimpan perubahan?", textAlign = TextAlign.Center)
            }
        }
    }

    // --- DIALOG BACK (Perubahan Belum Disimpan) ---
    if (showBackDialog) {
        com.example.notextra.ui.components.AppBaseDialog(
            title = "Perubahan Belum Disimpan",
            confirmText = "Simpan",
            dismissText = "Batal",
            onDismissRequest = { showBackDialog = false },
            onConfirm = {
                showBackDialog = false
                scope.launch {
                    kotlinx.coroutines.delay(150)
                    performSave()
                }
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.padding(bottom = 16.dp).height(48.dp).width(48.dp),
                    tint = AppDeleteColor
                )
                Text("Perubahan belum disimpan, apakah Anda ingin menyimpannya terlebih dahulu?", textAlign = TextAlign.Center)
            }
        }
    }
}

// --- KOMPONEN BANTUAN UNTUK TABEL ---
@Composable
fun TableCellHeader(text: String, width: Dp) {
    Box(modifier = Modifier.width(width).border(0.5.dp, Color.LightGray).padding(8.dp), contentAlignment = Alignment.Center) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
    }
}
@Composable
fun TableCellBody(text: String, width: Dp) {
    Box(modifier = Modifier.width(width).height(48.dp).border(0.5.dp, Color.LightGray).padding(8.dp), contentAlignment = Alignment.CenterStart) {
        Text(text = text, color = Color.Black, fontSize = 12.sp)
    }
}

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
        modifier = Modifier
            .width(width)
            .height(48.dp)
            .border(0.5.dp, Color.LightGray)
            .clickable { expanded = true }
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(badgeColor, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = status, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontSize = 12.sp) },
                    onClick = {
                        onStatusChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TableCellCheckbox(isChecked: Boolean, width: Dp, onCheckedChange: (Boolean) -> Unit) {
    Box(modifier = Modifier.width(width).height(48.dp).border(0.5.dp, Color.LightGray), contentAlignment = Alignment.Center) {
        Checkbox(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}