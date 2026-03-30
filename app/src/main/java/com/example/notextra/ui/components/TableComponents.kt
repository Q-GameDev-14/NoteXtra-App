package com.example.notextra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notextra.ui.theme.*
import androidx.compose.material3.Icon
import com.example.notextra.domain.model.DropdownOption

// ==========================================
// KOMPONEN SEL TABEL
// ==========================================

@Composable
fun TableCellHeader(text: String, width: Dp) {
    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            .heightIn(min = 48.dp)
            .background(Color(0xFFF8FAFC))
            .border(0.5.dp, Color(0xFFE2E8F0))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color(0xFF1E293B), fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun TableCellBody(text: String, width: Dp, isCenter: Boolean = false) {
    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            .heightIn(min = 48.dp)
            .background(Color.White)
            .border(0.5.dp, Color(0xFFE2E8F0))
            .padding(12.dp),
        contentAlignment = if (isCenter) Alignment.Center else Alignment.CenterStart
    ) {
        Text(
            text = text,
            color = if (isCenter) TextGray else Color(0xFF334155),
            fontSize = 13.sp,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 18.sp,
            fontWeight = if (isCenter) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun TableCellDropdown(status: String, width: Dp, onStatusChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Belum", "Selesai", "Proses", "Ditunda")

    val (bgColor, textColor) = when (status) {
        "Selesai" -> Pair(Color(0xFFDCFCE7), Color(0xFF166534))
        "Proses" -> Pair(Color(0xFFDBEAFE), Color(0xFF1E40AF))
        "Belum" -> Pair(Color(0xFFFEF3C7), Color(0xFF92400E))
        "Ditunda" -> Pair(Color(0xFFFEE2E2), Color(0xFF991B1B))
        else -> Pair(Color(0xFFF1F5F9), Color(0xFF475569))
    }

    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            .heightIn(min = 48.dp)
            .background(Color.White)
            .border(0.5.dp, Color(0xFFE2E8F0))
            .clickable { expanded = true }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .height(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(text = status, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(Color.White)) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option, fontSize = 13.sp, color = TextDark) }, onClick = { onStatusChange(option); expanded = false })
            }
        }
    }
}

@Composable
fun TableCellCheckbox(isChecked: Boolean, width: Dp, onCheckedChange: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            .heightIn(min = 48.dp)
            .background(Color.White)
            .border(0.5.dp, Color(0xFFE2E8F0)),
        contentAlignment = Alignment.Center
    ) {
        Checkbox(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}

// ==========================================
// SEL D DROPDOWN DINAMIS (UNTUK CUSTOM LIST)
// ==========================================
@Composable
fun TableCellDropdownCustom(
    text: String,
    dropdownOptions: List<DropdownOption>,
    width: Dp,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Cari opsi yang terpilih sekarang buat nyari warnanya
    val selectedOption = dropdownOptions.find { it.label == text }

    // Terjemahkan Hex String dari Database menjadi Color UI
    val badgeColor = try {
        Color(android.graphics.Color.parseColor(selectedOption?.colorHex ?: "#F1F5F9"))
    } catch (e: Exception) {
        Color.LightGray
    }

    // Sel yang bisa di-klik
    Box(
        modifier = Modifier
            .width(width)
            .height(50.dp) // Sesuaikan tinggi bawaanmu
            .border(width = (0.5).dp, color = Color.LightGray.copy(alpha = 0.5f)) // Style clean bawaanmu
            .clickable { expanded = true }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Tampilan Badge Status yang terpilih
        if (text.isNotBlank()) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeColor)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E3A8A))
            }
        }
        Icon(
            Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.size(16.dp).align(Alignment.CenterEnd),
            tint = TextGray
        )

        // Menu Dropdown-nya
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            dropdownOptions.forEach { opt ->
                // Terjemahkan warna opsi di menu
                val optBgColor = try { Color(android.graphics.Color.parseColor(opt.colorHex)) } catch (e: Exception) { Color.LightGray }

                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(optBgColor))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(opt.label, fontSize = 13.sp)
                        }
                    },
                    onClick = {
                        onValueChange(opt.label) // Kirim nama opsi barunya
                        expanded = false
                    }
                )
            }
        }
    }
}


// ==========================================
// SEL CHECKBOX DINAMIS (UNTUK CUSTOM LIST)
// ==========================================
@Composable
fun TableCellCheckboxCustom(
    isCheckedString: String, // Kita terima data "true" / "false" dari JSON Map
    width: Dp,
    onValueChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(50.dp)
            .border(width = (0.5).dp, color = Color.LightGray.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Checkbox(
            checked = isCheckedString == "true",
            onCheckedChange = { onValueChange(it) },
            colors = CheckboxDefaults.colors(checkedColor = FabColor)
        )
    }
}