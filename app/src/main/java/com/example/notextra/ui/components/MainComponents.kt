package com.example.notextra.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.notextra.domain.model.Note
import com.example.notextra.ui.theme.*
import com.example.notextra.utils.DateUtils

// =======================================================
// KOMPONEN UI PENDUKUNG MAIN SCREEN
// =======================================================

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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllNoteCardItem(note: Note, barColor: Color, onClick: () -> Unit, onLongClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp).combinedClickable(onClick = onClick, onLongClick = onLongClick).shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.width(4.dp).height(32.dp).clip(RoundedCornerShape(4.dp)).background(barColor))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickEditNoteDialog(note: Note, onDismiss: () -> Unit, onSave: (String, String, Boolean) -> Unit) {
    var judul by remember { mutableStateOf(note.title) }
    var kategori by remember { mutableStateOf(note.category) }
    var isPinned by remember { mutableStateOf(note.isPinned) }
    var expandedKategori by remember { mutableStateOf(false) }
    val opsiKategori = listOf("Work", "Personal", "Organization", "Idea")

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Quick edit", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(note.title, fontSize = 14.sp, color = FabColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFF3F4F6)).clickable { onDismiss() }, contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextGray, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(value = judul, onValueChange = { judul = it }, label = { Text("TITLE", fontSize = 12.sp, fontWeight = FontWeight.Bold) }, maxLines = 2, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FabColor, unfocusedBorderColor = Color.LightGray))
                Spacer(modifier = Modifier.height(16.dp))

                Box {
                    OutlinedTextField(value = kategori, onValueChange = {}, readOnly = true, label = { Text("CATEGORY", fontSize = 12.sp, fontWeight = FontWeight.Bold) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Dropdown", modifier = Modifier.clickable { expandedKategori = true }) }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FabColor, unfocusedBorderColor = Color.LightGray))
                    DropdownMenu(expanded = expandedKategori, onDismissRequest = { expandedKategori = false }) {
                        opsiKategori.forEach { opt -> DropdownMenuItem(text = { Row(verticalAlignment = Alignment.CenterVertically) { Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(getCategoryColor(opt))); Spacer(modifier = Modifier.width(12.dp)); Text(opt, color = TextDark) } }, onClick = { kategori = opt; expandedKategori = false }) }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxWidth().border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)).clip(RoundedCornerShape(12.dp)).clickable { isPinned = !isPinned }.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Pinned", color = TextDark, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            Text("Visible on home screen", color = TextGray, fontSize = 14.sp)
                        }
                        Switch(checked = isPinned, onCheckedChange = { isPinned = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = FabColor, uncheckedThumbColor = Color.White, uncheckedTrackColor = Color.LightGray, uncheckedBorderColor = Color.Transparent))
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color.LightGray)) { Text("Batal", color = FabColor, fontWeight = FontWeight.Bold) }
                    Button(onClick = { onSave(judul, kategori, isPinned) }, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = FabColor)) { Text("Simpan", color = Color.White, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

fun getCategoryColor(category: String): Color {
    return when(category) {
        "Work" -> CardBlue
        "Personal" -> CardGreen
        "Organization" -> CardOrange
        "Idea" -> TableAccentColor
        else -> CardBlue
    }
}

// ==========================================
// DATA CLASS UNTUK CUSTOM TABLE
// ==========================================
data class DropdownOption(val label: String, val color: Color)

data class ColumnConfig(
    var name: String = "",
    var width: String = "Normal", // Narrow, Normal, Wide
    var type: String = "Text",    // Text, Dropdown, Checkbox, Date, Image
    var dropdownOptions: List<DropdownOption> = emptyList()
)

// ==========================================
// WIZARD DIALOG CREATE LIST (4 STEPS)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListDialog(
    onDismiss: () -> Unit,
    // Callback sekarang menerima List<ColumnConfig> untuk Custom Table
    onCreate: (String, String, String, List<ColumnConfig>) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }

    // State Step 1 & 2
    var selectedType by remember { mutableStateOf("Default") }
    var listTitle by remember { mutableStateOf("") }
    var listCategory by remember { mutableStateOf("Work") }
    var expandedCategory by remember { mutableStateOf(false) }
    val kategoriOptions = listOf("Work", "Personal", "Organization", "Idea")

    // State Step 3 (Structure)
    var numRows by remember { mutableIntStateOf(3) }
    var numCols by remember { mutableIntStateOf(3) }
    var sizingMode by remember { mutableStateOf("Auto") } // Auto atau Manual
    var autoSize by remember { mutableStateOf("Normal") } // Narrow, Normal, Wide

    // State Step 4 (Column Setup)
    // Akan diinisialisasi saat masuk ke Step 4 berdasarkan numCols
    var columnConfigs by remember { mutableStateOf(listOf<ColumnConfig>()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().heightIn(max = 650.dp), // Batasi tinggi agar bisa di-scroll kalau kepanjangan
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.width(40.dp).height(4.dp).clip(CircleShape).background(Color.LightGray.copy(alpha = 0.5f)))
                Spacer(modifier = Modifier.height(16.dp))

                // ==========================================
                // STEP 1: CHOOSE TYPE
                // ==========================================
                if (step == 1) {
                    Text("Tambah List", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text("Step 1 of ${if(selectedType == "Custom") 4 else 2} — choose type", fontSize = 14.sp, color = FabColor)
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        TypeSelectionCard(modifier = Modifier.weight(1f), title = "Default", subtitle = "Ready-made", icon = Icons.Default.List, isSelected = selectedType == "Default", onClick = { selectedType = "Default" })
                        TypeSelectionCard(modifier = Modifier.weight(1f), title = "Custom", subtitle = "Define columns", icon = Icons.Default.Build, isSelected = selectedType == "Custom", onClick = { selectedType = "Custom" })
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(onClick = { step = 2 }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = FabColor), shape = RoundedCornerShape(12.dp)) {
                        Text("Continue →", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onDismiss) { Text("Cancel", color = FabColor, fontWeight = FontWeight.Bold) }
                }

                // ==========================================
                // STEP 2: TITLE & CATEGORY
                // ==========================================
                else if (step == 2) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Back", tint = TextGray, modifier = Modifier.clickable { step = 1 })
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Back", color = TextGray, fontSize = 14.sp, modifier = Modifier.clickable { step = 1 })
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Name your list", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.fillMaxWidth())
                    Text("Step 2 of ${if(selectedType == "Custom") 4 else 2} — $selectedType Table", fontSize = 14.sp, color = FabColor, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(value = listTitle, onValueChange = { listTitle = it }, label = { Text("List title") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FabColor, unfocusedBorderColor = FabColor))
                    Spacer(modifier = Modifier.height(24.dp))

                    Box {
                        OutlinedTextField(value = listCategory, onValueChange = {}, readOnly = true, label = { Text("Category") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Dropdown", modifier = Modifier.clickable { expandedCategory = true }) }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FabColor, unfocusedBorderColor = FabColor))
                        DropdownMenu(expanded = expandedCategory, onDismissRequest = { expandedCategory = false }) {
                            kategoriOptions.forEach { opt -> DropdownMenuItem(text = { Row(verticalAlignment = Alignment.CenterVertically) { Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(getCategoryColor(opt))); Spacer(modifier = Modifier.width(12.dp)); Text(opt, color = TextDark) } }, onClick = { listCategory = opt; expandedCategory = false }) }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (listTitle.isNotBlank()) {
                                if (selectedType == "Default") {
                                    onCreate("Default", listTitle, listCategory, emptyList())
                                } else {
                                    step = 3 // Lanjut ke konfigurasi Custom Table
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = FabColor), shape = RoundedCornerShape(12.dp)
                    ) { Text(if (selectedType == "Default") "Buat List" else "Continue →", fontWeight = FontWeight.Bold) }
                }

                // ==========================================
                // STEP 3: STRUCTURE (CUSTOM TABLE ONLY)
                // ==========================================
                else if (step == 3) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Back", tint = TextGray, modifier = Modifier.clickable { step = 2 })
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Back", color = TextGray, fontSize = 14.sp, modifier = Modifier.clickable { step = 2 })
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Table size", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.fillMaxWidth())
                    Text("Step 3 of 4 — structure", fontSize = 14.sp, color = FabColor, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(24.dp))

                    // --- TAMBAHKAN BLUE INFO BLOCK DI SINI ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp)) // Biru sangat muda
                            .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(12.dp)) // Garis tepi biru muda
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = Color(0xFF1E40AF), // Biru tua
                            modifier = Modifier.size(20.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = androidx.compose.ui.text.buildAnnotatedString {
                                withStyle(style = androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1E40AF))) {
                                    append("Auto-number column")
                                }
                                withStyle(style = androidx.compose.ui.text.SpanStyle(color = Color(0xFF1E3A8A))) {
                                    append(" — every table always has a built-in # column that numbers rows automatically. The columns you set here are ")
                                }
                                withStyle(style = androidx.compose.ui.text.SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, color = Color(0xFF1E3A8A))) {
                                    append("in addition")
                                }
                                withStyle(style = androidx.compose.ui.text.SpanStyle(color = Color(0xFF1E3A8A))) {
                                    append(" to that.")
                                }
                            },
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // Counter Baris & Kolom
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Starting rows", color = TextDark, fontWeight = FontWeight.Medium)
                        CounterView(value = numRows, onValueChange = { if (it in 1..20) numRows = it })
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Your columns", color = TextDark, fontWeight = FontWeight.Medium)
                        CounterView(value = numCols, onValueChange = { if (it in 1..10) numCols = it })
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Sizing Mode (Auto vs Manual)
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Sizing Mode", color = TextDark, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text(if (sizingMode == "Auto") "Semua kolom disamakan" else "Atur satu per satu", fontSize = 11.sp, color = TextGray)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth().height(40.dp).border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)).clip(RoundedCornerShape(8.dp))) {
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(if (sizingMode == "Auto") Color(0xFFE0E7FF) else Color.Transparent).clickable { sizingMode = "Auto" }, contentAlignment = Alignment.Center) {
                            Text("Auto", color = if (sizingMode == "Auto") FabColor else TextGray, fontWeight = FontWeight.Bold)
                        }
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(if (sizingMode == "Manual") Color(0xFFE0E7FF) else Color.Transparent).clickable { sizingMode = "Manual" }, contentAlignment = Alignment.Center) {
                            Text("Manual", color = if (sizingMode == "Manual") FabColor else TextGray, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Pilihan Size jika Auto
                    if (sizingMode == "Auto") {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth().height(40.dp).border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)).clip(RoundedCornerShape(8.dp))) {
                            listOf("Narrow", "Normal", "Wide").forEach { size ->
                                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(if (autoSize == size) FabColor else Color.Transparent).clickable { autoSize = size }, contentAlignment = Alignment.Center) {
                                    Text(size, color = if (autoSize == size) Color.White else TextGray, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            // Siapkan data kolom untuk Step 4
                            if (columnConfigs.size != numCols) {
                                columnConfigs = List(numCols) { index ->
                                    ColumnConfig(name = "Col ${index + 1}", width = if (sizingMode == "Auto") autoSize else "Normal")
                                }
                            } else if (sizingMode == "Auto") {
                                // Update ukuran jika user mengganti mode ke Auto
                                columnConfigs = columnConfigs.map { it.copy(width = autoSize) }
                            }
                            step = 4
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = FabColor), shape = RoundedCornerShape(12.dp)
                    ) { Text("Next →", fontWeight = FontWeight.Bold) }
                }

                // ==========================================
                // STEP 4: COLUMN SETUP (CUSTOM TABLE ONLY)
                // ==========================================
                else if (step == 4) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Back", tint = TextGray, modifier = Modifier.clickable { step = 3 })
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Back to structure", color = TextGray, fontSize = 14.sp, modifier = Modifier.clickable { step = 3 })
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Column Setup", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.fillMaxWidth())
                    Text("Step 4 of 4 — name & types", fontSize = 14.sp, color = FabColor, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))

                    // Scrollable Area untuk Konfigurasi Kolom
                    Column(modifier = Modifier.fillMaxWidth()) {
                        columnConfigs.forEachIndexed { index, colConfig ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Box(modifier = Modifier.background(Color(0xFFE0E7FF), RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                                        Text("Column ${index + 1}", color = FabColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))

                                    // 1. NAMA KOLOM
                                    OutlinedTextField(
                                        value = colConfig.name,
                                        onValueChange = { newName ->
                                            columnConfigs = columnConfigs.toMutableList().also { it[index] = colConfig.copy(name = newName) }
                                        },
                                        label = { Text("Nama Kolom", fontSize = 12.sp) },
                                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FabColor, unfocusedBorderColor = Color.LightGray)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    // 2. UKURAN KOLOM (Disabled jika Auto)
                                    Text("Width", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth().height(36.dp).border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)).clip(RoundedCornerShape(8.dp))) {
                                        listOf("Narrow", "Normal", "Wide").forEach { size ->
                                            val isSelected = colConfig.width == size
                                            val isDisabled = sizingMode == "Auto"
                                            val bgColor = if (isSelected) (if(isDisabled) Color.Gray else FabColor) else Color.Transparent
                                            val textColor = if (isSelected) Color.White else (if(isDisabled) Color.LightGray else TextGray)

                                            Box(
                                                modifier = Modifier.weight(1f).fillMaxHeight().background(bgColor)
                                                    .clickable(enabled = !isDisabled) {
                                                        columnConfigs = columnConfigs.toMutableList().also { it[index] = colConfig.copy(width = size) }
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) { Text(size, color = textColor, fontSize = 12.sp) }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))

                                    // 3. TIPE KOLOM
                                    var typeExpanded by remember { mutableStateOf(false) }
                                    val typeOptions = listOf("Text", "Dropdown", "Checkbox", "Date", "Image")
                                    Box {
                                        OutlinedTextField(
                                            value = colConfig.type, onValueChange = {}, readOnly = true, label = { Text("Tipe Data", fontSize = 12.sp) }, modifier = Modifier.fillMaxWidth(),
                                            trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.clickable { typeExpanded = true }) },
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FabColor, unfocusedBorderColor = Color.LightGray)
                                        )
                                        DropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                                            typeOptions.forEach { opt ->
                                                DropdownMenuItem(text = { Text(opt) }, onClick = {
                                                    columnConfigs = columnConfigs.toMutableList().also { it[index] = colConfig.copy(type = opt) }
                                                    typeExpanded = false
                                                })
                                            }
                                        }
                                    }

                                    // 4. LOGIKA KHUSUS: JIKA DROPDOWN
                                    if (colConfig.type == "Dropdown") {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("Opsi Dropdown (Contoh: Selesai, Pending)", fontSize = 11.sp, color = FabColor)
                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Menampilkan opsi yang sudah ada
                                        colConfig.dropdownOptions.forEachIndexed { optIndex, opt ->
                                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(opt.color))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(opt.label, fontSize = 13.sp, color = TextDark, modifier = Modifier.weight(1f))
                                            }
                                        }

                                        // Tombol tambah opsi (Sementara dummy UI)
                                        OutlinedButton(
                                            onClick = { /* TODO: Buka dialog tambah warna & teks */ },
                                            modifier = Modifier.fillMaxWidth().height(36.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        ) { Text("+ Tambah Opsi", fontSize = 12.sp) }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            // TODO: Di tahap berikutnya, kita proses data columnConfigs jadi JSON
                            onCreate(selectedType, listTitle, listCategory, columnConfigs)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = FabColor), shape = RoundedCornerShape(12.dp)
                    ) { Text("Buat Custom Table", fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

// Komponen Pembantu untuk Step 3
@Composable
fun CounterView(value: Int, onValueChange: (Int) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(36.dp) // <--- INI KUNCINYA: Kita gembok tingginya di 36.dp biar nggak molor
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF8FAFC))
    ) {
        Box(modifier = Modifier.size(36.dp).clickable { onValueChange(value - 1) }, contentAlignment = Alignment.Center) {
            Text("-", fontSize = 18.sp, color = TextGray)
        }
        Box(modifier = Modifier.width(40.dp).fillMaxHeight(), contentAlignment = Alignment.Center) {
            Text(value.toString(), fontWeight = FontWeight.Bold, color = TextDark)
        }
        Box(modifier = Modifier.size(36.dp).clickable { onValueChange(value + 1) }, contentAlignment = Alignment.Center) {
            Text("+", fontSize = 18.sp, color = TextGray)
        }
    }
}

@Composable
fun TypeSelectionCard(modifier: Modifier = Modifier, title: String, subtitle: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
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

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("TITLE", fontSize = 12.sp, fontWeight = FontWeight.Bold) }, modifier = Modifier.fillMaxWidth(), maxLines = 2, shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FabColor, unfocusedBorderColor = Color.LightGray))
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("CONTENT", fontSize = 12.sp, fontWeight = FontWeight.Bold) }, modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp), minLines = 3, shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FabColor, unfocusedBorderColor = Color.LightGray))
                Spacer(modifier = Modifier.height(16.dp))

                Box {
                    OutlinedTextField(value = category, onValueChange = {}, readOnly = true, label = { Text("CATEGORY", fontSize = 12.sp, fontWeight = FontWeight.Bold) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Dropdown", modifier = Modifier.clickable { expandedCategory = true }) }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FabColor, unfocusedBorderColor = Color.LightGray))
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