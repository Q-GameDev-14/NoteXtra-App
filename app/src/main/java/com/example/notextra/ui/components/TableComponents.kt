package com.example.notextra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
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