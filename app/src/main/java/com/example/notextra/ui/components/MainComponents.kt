package com.example.notextra.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
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
                    TextButton(onClick = onDismiss) { Text("Cancel", color = FabColor, fontWeight = FontWeight.Bold) }
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
                    OutlinedTextField(value = listTitle, onValueChange = { listTitle = it }, label = { Text("List title") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FabColor, unfocusedBorderColor = FabColor))
                    Spacer(modifier = Modifier.height(24.dp))
                    Box {
                        OutlinedTextField(value = listCategory, onValueChange = {}, readOnly = true, label = { Text("Category") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Dropdown", modifier = Modifier.clickable { expandedCategory = true }) }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FabColor, unfocusedBorderColor = FabColor))
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
                    TextButton(onClick = onDismiss) { Text("Cancel", color = FabColor, fontWeight = FontWeight.Bold) }
                }
            }
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