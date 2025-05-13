package com.example.moncloaplus.screens.create_event

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.moncloaplus.R
import com.example.moncloaplus.data.model.ActividadesColegiales
import com.example.moncloaplus.data.model.ClubesProfesionales
import com.example.moncloaplus.data.model.EventType
import com.example.moncloaplus.screens.reservation.AdvancedTimePickerDialog
import com.example.moncloaplus.screens.reservation.DatePickerModal
import com.example.moncloaplus.screens.reservation.formatHourMinute
import com.example.moncloaplus.screens.reservation.toFormattedDate
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTypeSelector(
    selectedType: EventType,
    selectedSubCategory: Any?,
    onTypeSelected: (EventType) -> Unit,
    onSubCategorySelected: (EventType, Any) -> Unit,
    enabled: Boolean
) {
    var expandedType by remember { mutableStateOf<EventType?>(null) }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(eventTypeNameMap.entries.toList()) { (type, label) ->

            val isExpandable = type == EventType.ACTIVIDAD_COLEGIAL || type == EventType.CLUBES_PROFESIONALES
            val isExpanded = expandedType == type

            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = {
                    if (enabled) {
                        if (isExpandable) {
                            expandedType = if (isExpanded) null else type
                        } else {
                            onTypeSelected(type)
                            expandedType = null
                        }
                    }
                },
                modifier = Modifier
                    .padding(
                        start = if (type == eventTypeNameMap.keys.first()) 24.dp else 0.dp,
                        end = if (type == eventTypeNameMap.keys.last()) 8.dp else 0.dp
                    )
            ) {
                val displayLabel = when {
                    selectedType == EventType.ACTIVIDAD_COLEGIAL && selectedSubCategory is ActividadesColegiales ->
                        actividadesColegialesNameMap[selectedSubCategory] ?: label
                    selectedType == EventType.CLUBES_PROFESIONALES && selectedSubCategory is ClubesProfesionales ->
                        clubesProfesionalesNameMap[selectedSubCategory] ?: label
                    else -> label
                }

                AssistChip(
                    onClick = {
                        if (enabled) {
                            if (isExpandable) {
                                expandedType = if (isExpanded) null else type
                            } else {
                                onTypeSelected(type)
                                expandedType = null
                            }
                        }
                    },
                    enabled = enabled,
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (selectedType == type) displayLabel else label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    trailingIcon = {
                        if (isExpandable) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowDropDown,
                                contentDescription = "Seleccionar subcategoría",
                                tint = when {
                                    !enabled -> {
                                        if (selectedType == type)
                                            MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f)
                                        else
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                                    }
                                    selectedType == type -> MaterialTheme.colorScheme.onTertiaryContainer
                                    else -> MaterialTheme.colorScheme.outline
                                }
                            )
                        }
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selectedType == type) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surface,
                        labelColor = if (selectedType == type) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.outline,
                        disabledContainerColor = if (selectedType == type) MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                        disabledLabelColor = if (selectedType == type) MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                    ),
                    border = if (selectedType == type) null else BorderStroke(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { expandedType = null }
                ) {
                    when (type) {
                        EventType.ACTIVIDAD_COLEGIAL -> {
                            ActividadesColegiales.entries.forEach { subType ->
                                DropdownMenuItem(
                                    text = { Text(actividadesColegialesNameMap[subType] ?: "") },
                                    onClick = {
                                        onSubCategorySelected(type, subType)
                                        expandedType = null
                                    }
                                )
                            }
                        }
                        EventType.CLUBES_PROFESIONALES -> {
                            ClubesProfesionales.entries.forEach { subType ->
                                DropdownMenuItem(
                                    text = { Text(clubesProfesionalesNameMap[subType] ?: "") },
                                    onClick = {
                                        onSubCategorySelected(type, subType)
                                        expandedType = null
                                    }
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun AllDayOption(
    isAllDay: Boolean,
    onAllDayChange: (Boolean) -> Unit,
    enabled: Boolean
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
            onCheckedChange = onAllDayChange,
            enabled = enabled
        )
    }
}

@Composable
fun SelectDatePicker(
    selectedDate: Long,
    onDateSelected: (Long) -> Unit,
    enabled: Boolean
) {
    var showDatePicker by remember { mutableStateOf(false) }

    TextButton(
        onClick = { showDatePicker = true },
        modifier = Modifier.padding(start = 36.dp),
        enabled = enabled
    ) {
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
    enabled: Boolean
) {
    var showTimePicker by remember { mutableStateOf(false) }

    TextButton(
        onClick = { showTimePicker = true },
        enabled = enabled
    ) {
        Text(
            text = formatHourMinute(selectedTime.first, selectedTime.second),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LabeledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    iconResId: Int,
    contentDescription: String,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .bringIntoViewRequester(bringIntoViewRequester)
    ) {
        BasicTextField(
            value = value,
            onValueChange =  {
                onValueChange(it)
                coroutineScope.launch {
                    bringIntoViewRequester.bringIntoView()
                }
            },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences,
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
    enabled: Boolean,
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
                    enabled = enabled,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        val updated = displayedSpeakers.toMutableList()
                        updated.removeAt(index)
                        onSpeakersChange(updated)
                    },
                    enabled = enabled
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
                .clickable(enabled = enabled && displayedSpeakers.last().isNotBlank()) {
                    val updated = displayedSpeakers.toMutableList()
                    updated.add("")
                    onSpeakersChange(updated)
                }
        )
    }
}
