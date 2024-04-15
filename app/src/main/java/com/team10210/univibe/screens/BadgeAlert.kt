package com.team10210.univibe.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter

@Composable
fun BadgeDialog(
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    iconUrl: String,
) {
    AlertDialog(
        icon = {
            Image(
                painter = rememberImagePainter(
                    data = iconUrl,
                    builder = {
                        crossfade(true)

                    }
                ),
                contentDescription = "test",
                modifier = Modifier.fillMaxWidth().aspectRatio(1f), // Adjust size as needed
                contentScale = ContentScale.Fit
            )
        },
        title = {
            Text(text = dialogTitle, style = TextStyle(fontSize = 16.sp))
        },
        text = {
            Text(text = dialogText, textAlign = TextAlign.Center)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = null,
        onDismissRequest = {}
    )
}