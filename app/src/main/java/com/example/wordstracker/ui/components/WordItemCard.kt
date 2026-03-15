package com.example.wordstracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.wordstracker.data.VocabWord

@Composable
fun WordItemCard(
    wordItem: VocabWord,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onEditClick() }
            ) {
                Text(
                    text = wordItem.word,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (wordItem.isLearned) TextDecoration.LineThrough else TextDecoration.None
                )

                Text(
                    text = wordItem.translation,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Checkbox(
                checked = wordItem.isLearned,
                onCheckedChange = { isChecked ->
                    onCheckedChange(isChecked)
                }
            )

            IconButton(onClick = { onDeleteClick() }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete word",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}