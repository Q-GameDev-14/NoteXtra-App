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
import com.example.notextra.ui.theme.DialogBackgroundColor
import com.example.notextra.ui.theme.DialogButtonColor

@Composable
fun AppBaseDialog(
    title: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    confirmText: String = "Simpan",
    dismissText: String = "Batal",
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
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
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                content()

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onDismissRequest,
                        // Mengambil warna dari Color.kt
                        colors = ButtonDefaults.buttonColors(containerColor = DialogButtonColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text(dismissText, color = Color.White)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = onConfirm,
                        // Mengambil warna dari Color.kt
                        colors = ButtonDefaults.buttonColors(containerColor = DialogButtonColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text(confirmText, color = Color.White)
                    }
                }
            }
        }
    }
}