package com.example.moncloaplus.screens.create_event

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moncloaplus.R
import com.example.moncloaplus.screens.reservation.DatePickerModal
import com.example.moncloaplus.screens.reservation.toFormattedDate

@Composable
fun SaveEventButton(
    onSave: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {

    Button (
        modifier = modifier,
        onClick = { if (enabled) onSave() },
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = MaterialTheme.shapes.extraLarge,
        contentPadding = PaddingValues(horizontal = 16.dp),
        enabled = enabled
    ) {
        Text(text = "Guardar")
    }

}

@Composable
fun EventTypeSelector(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(EVENT_TYPES) { index, type ->
            val isFirst = index == 0
            val isLast = index == EVENT_TYPES.lastIndex

            AssistChip(
                onClick = { onTypeSelected(type) },
                label = {
                    Text(
                        type,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (selectedType == type) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surface,
                    labelColor = if (selectedType == type) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.outline
                ),
                border = if (selectedType == type) null else BorderStroke(
                    width = 0.75.dp,
                    color = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.padding(
                    start = if (isFirst) 24.dp else 0.dp,
                    end = if (isLast) 8.dp else 0.dp
                )
            )
        }
    }
}

@Composable
fun AllDayOption(
    isAllDay: Boolean,
    onAllDayChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.schedule_24px),
            contentDescription = "Todo el día",
            tint = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Todo el día",
            modifier = Modifier.padding(start = 24.dp),
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.weight(1f))

        Switch(
            checked = isAllDay,
            onCheckedChange = onAllDayChange
        )
    }
}

@Composable
fun SelectDatePicker(
    selectedDate: Long,
    onDateSelected: (Long) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    TextButton(onClick = { showDatePicker = true }, modifier = Modifier.padding(start = 36.dp)) {
        Text(text = selectedDate.toFormattedDate(), fontSize = 18.sp)
    }

    if (showDatePicker) {
        DatePickerModal(
            initialDate = selectedDate,
            onDateSelected = {
                onDateSelected(it)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

}
