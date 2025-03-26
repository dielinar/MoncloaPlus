package com.example.moncloaplus.screens.fixes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleChoiceSegmentedButton(
    options: List<String>,
    onOptionClick: (String) -> Unit
) {
    var selectedOption by remember { mutableStateOf(options[FIXED_INDEX]) }

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.scale(0.9f)
    ) {
        options.forEachIndexed { index, label ->
            val isSelected = selectedOption == label

            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size,
                    baseShape = MaterialTheme.shapes.extraLarge
                ),
                onClick = {
                    selectedOption = label
                    onOptionClick(label)
                },
                selected = isSelected,
                label = { Text(label) },
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = FIXES_CONTAINER_COLORS[index],
                    activeContentColor = MaterialTheme.colorScheme.scrim
                )
            )
        }
    }
}

@Composable
fun NewFixButton() {
    var showDialog by remember { mutableStateOf(false) }

    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        onClick = { showDialog = true }
    ) { Icon(Icons.Filled.Add, null) }

    if (showDialog) {}

}
