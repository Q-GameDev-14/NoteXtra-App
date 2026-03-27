package com.example.notextra.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.notextra.ui.theme.AppDeleteColor
import com.example.notextra.ui.theme.DialogBackgroundColor
import com.example.notextra.ui.theme.DialogButtonColor

/**
 * [AppBaseDialog] adalah cetakan dasar (Template) untuk semua dialog peringatan di aplikasi.
 * Dengan menggunakan ini, desain dialog di seluruh aplikasi akan seragam dan konsisten.*/
@Composable
fun AppBaseDialog(
    title: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onDiscard: (() -> Unit)? = null,
    confirmText: String = "Save",
    dismissText: String = "Cancel",
    discardText: String = "Discard",
    isConfirmEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DialogBackgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- SECTION 1: HEADER (JUDUL) ---
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // --- SECTION 2: KONTEN KUSTOM ---
                // Menampilkan UI apapun yang disisipkan saat fungsi ini dipanggil
                content()

                Spacer(modifier = Modifier.height(24.dp))

                // --- SECTION 3: AREA TOMBOL (ACTION BUTTONS) ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Tombol 1: Buang / Discard
                    if (onDiscard != null) {
                        Button(
                            onClick = onDiscard,
                            colors = ButtonDefaults.buttonColors(containerColor = AppDeleteColor),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(48.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
                        ) {
                            Text(discardText, color = Color.White, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Tombol 2: Batal / Lanjut Edit
                    Button(
                        onClick = onDismissRequest,
                        colors = ButtonDefaults.buttonColors(containerColor = DialogButtonColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(48.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
                    ) {
                        Text(dismissText, color = Color.White, fontSize = 11.sp, textAlign = TextAlign.Center)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Tombol 3: Simpan
                    Button(
                        onClick = onConfirm,
                        enabled = isConfirmEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DialogButtonColor,
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Color.DarkGray
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(48.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
                    ) {
                        Text(confirmText, color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}