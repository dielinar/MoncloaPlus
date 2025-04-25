package com.example.moncloaplus.screens.create_event

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.moncloaplus.R
import com.example.moncloaplus.screens.reservation.AdvancedTimePickerDialog
import com.example.moncloaplus.screens.reservation.DatePickerModal
import com.example.moncloaplus.screens.reservation.formatHourMinute
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
                    width = 0.5.dp,
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
            style = MaterialTheme.typography.bodyLarge,
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
        Text(text = selectedDate.toFormattedDate(), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTimePicker(
    selectedTime: Pair<Int, Int>,
    onTimeSelected: (Pair<Int, Int>) -> Unit,
    validationError: String? = null
) {
    var showTimePicker by remember { mutableStateOf(false) }

    TextButton(onClick = { showTimePicker = true }) {
        Text(
            text = formatHourMinute(selectedTime.first, selectedTime.second),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (validationError != null)
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.primary
        )
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedTime.first,
            initialMinute = selectedTime.second,
            is24Hour = true
        )
        var showDial by remember { mutableStateOf(true) }
        val toggleIcon = if (showDial) R.drawable.keyboard_24px else R.drawable.schedule_24px

        AdvancedTimePickerDialog(
            onDismiss = { showTimePicker = false },
            onConfirm = {
                onTimeSelected(Pair(timePickerState.hour, timePickerState.minute))
                showTimePicker = false
            },
            toggle = {
                IconButton(onClick = { showDial = !showDial }) {
                    Icon(
                        painter = painterResource(toggleIcon),
                        contentDescription = "Alternar tipo de selector"
                    )
                }
            }
        ) {
            if (showDial) {
                TimePicker(state = timePickerState)
            } else {
                TimeInput(state = timePickerState)
            }
        }
    }
}

@Composable
fun LabeledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    iconResId: Int,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Box(modifier = modifier) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(iconResId),
                        contentDescription = contentDescription,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 24.dp)
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )
    }
}

@Composable
fun MultipleSpeakersField(
    speakers: List<String>,
    onSpeakersChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val displayedSpeakers = speakers.ifEmpty { listOf("") }

    Column(modifier = modifier) {
        displayedSpeakers.forEachIndexed { index, name ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                LabeledTextField(
                    value = name,
                    onValueChange = { newName ->
                        val updated = displayedSpeakers.toMutableList()
                        updated[index] = newName
                        onSpeakersChange(updated)
                    },
                    placeholder = "Nombre del ponente",
                    iconResId = R.drawable.person_24px,
                    contentDescription = "Ponente",
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        val updated = displayedSpeakers.toMutableList()
                        updated.removeAt(index)
                        onSpeakersChange(updated)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Eliminar ponente",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Text(
            text = "+ añadir ponente",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier
                .padding(start = 48.dp, top = 4.dp)
                .clickable {
                    val updated = displayedSpeakers.toMutableList()
                    updated.add("")
                    onSpeakersChange(updated)
                }
        )
    }
}
