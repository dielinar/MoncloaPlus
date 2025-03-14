package com.example.moncloaplus.screens.reservation

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.moncloaplus.R
import com.example.moncloaplus.model.ReservType
import com.example.moncloaplus.model.ReservationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDialog(
    index: Int,
    viewModel: ReservationViewModel,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    val newDate by viewModel.newDate.collectAsState()
    val startTime by viewModel.startTime.collectAsState()
    val endTime by viewModel.endTime.collectAsState()
    val note by viewModel.note.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        val focusManager = LocalFocusManager.current

        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp
        ) {
            Column (horizontalAlignment = Alignment.CenterHorizontally)
            {
                Spacer(modifier = Modifier.height(36.dp))
                Icon(
                    painter = painterResource(RESERVATION_ICONS[index]),
                    tint = MaterialTheme.colorScheme.tertiary,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text (
                    modifier = Modifier.padding(bottom = 48.dp),
                    text = RESERVATION_NAMES[index],
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                HorizontalDivider(modifier = Modifier.padding(bottom = 6.dp), thickness = 2.dp)

                Row (
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        "Selecciona día:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    TextButton(onClick = {showDatePicker = true})
                    {
                        Text(text = newDate.toFormattedDate(), fontSize = 16.sp)
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(top = 6.dp, bottom = 6.dp), thickness = 2.dp)

                Row (
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        "Hora de inicio:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    TextButton(onClick = {showStartTimePicker = true}) {
                        Text(text = startTime.let { formatHourMinute(it.first, it.second) }, fontSize = 16.sp)
                    }
                }

                Row (
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        "Hora de fin:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    TextButton(onClick = {showEndTimePicker = true}) {
                        Text(text = endTime.let { formatHourMinute(it.first, it.second) }, fontSize = 16.sp)
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(top = 6.dp), thickness = 2.dp)

                OutlinedTextField(
                    value = note,
                    onValueChange = { viewModel.updateNote(it) },
                    label = { Text("Nota", style = MaterialTheme.typography.labelMedium) },
                    modifier = Modifier.fillMaxWidth().focusable(true).padding(top = 24.dp, start = 24.dp, end = 24.dp),
                    maxLines = 3,
                    singleLine = false,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, end = 24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancelar", color = MaterialTheme.colorScheme.tertiary, fontSize = 16.sp) }
                    TextButton(onClick = onConfirm) { Text("Guardar", color = MaterialTheme.colorScheme.tertiary, fontSize = 16.sp) }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerModal(
            newDate,
            onDateSelected = { selectedDate ->
                viewModel.updateNewDate(selectedDate)
                showDatePicker = false },
            onDismiss = { showDatePicker = false}
        )
    }

    if (showStartTimePicker) {
        AdvancedTimePicker(
            viewModel,
            isStartTime = true,
            onConfirm = { state ->
                viewModel.updateStartTime(Pair(state.hour, state.minute))
                showStartTimePicker = false },
            onDismiss = { showStartTimePicker = false}
        )
    }

    if (showEndTimePicker) {
        AdvancedTimePicker(
            viewModel,
            isStartTime = false,
            onConfirm = { state ->
                viewModel.updateEndTime(Pair(state.hour, state.minute))
                showEndTimePicker = false
            },
            onDismiss = { showEndTimePicker = false }
        )
    }

}

@Composable
fun NewReservationButton(
    type: Int,
    viewModel: ReservationViewModel
) {
    var showDialog by remember { mutableStateOf(false) }

    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        onClick = { showDialog = true }
    ) { Icon(Icons.Filled.Add, null) }

    if (showDialog) {
        ReservationDialog(
            type,
            viewModel,
            onDismiss = { showDialog = false },
            onConfirm = {
                viewModel.createReservation(ReservType.entries[type])
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    initialDate: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate)

    val confirmEnabled = remember { derivedStateOf { datePickerState.selectedDateMillis != null } }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { datePickerState.selectedDateMillis?.let { onDateSelected(it) } },
                enabled = confirmEnabled.value
            ) { Text("Aceptar") }
        },
        dismissButton = { TextButton(onClick = { onDismiss() }) { Text("Cancelar") } }
    ) {
        DatePicker(
            state = datePickerState,
            modifier = Modifier.verticalScroll(rememberScrollState())
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedTimePicker(
    viewModel: ReservationViewModel,
    isStartTime: Boolean,
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit
) {
    val timeState by if (isStartTime) viewModel.startTime.collectAsState() else viewModel.endTime.collectAsState()
    val defaultHour = timeState.first
    val defaultMinute = timeState.second

    val timePickerState = rememberTimePickerState(
        initialHour = defaultHour,
        initialMinute = defaultMinute,
        is24Hour = true
    )

    var showDial by remember { mutableStateOf(true) }

    val toggleIcon: Int = if (showDial) R.drawable.keyboard_24px else R.drawable.schedule_24px

    AdvancedTimePickerDialog(
        onDismiss = onDismiss,
        onConfirm = { onConfirm(timePickerState) },
        toggle = {
            IconButton(onClick = { showDial = !showDial }) {
                Icon(
                    painter = painterResource(toggleIcon),
                    contentDescription = "Time picker type toggle"
                )
            }
        }
    ) {
        if (showDial) TimePicker(state = timePickerState)
        else TimeInput(state = timePickerState)
    }

}

@Composable
fun AdvancedTimePickerDialog(
    title: String = "Seleccionar hora",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    TextButton(onClick = onConfirm) { Text("Aceptar") }
                }
            }
        }
    }
}

@Composable
fun MyReservationsButton(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit
) {
    FilterChip(
        onClick = { onSelectedChange(!selected) },
        colors = FilterChipDefaults.filterChipColors(
            selectedLeadingIconColor = MaterialTheme.colorScheme.tertiaryContainer,
            selectedContainerColor = MaterialTheme.colorScheme.onTertiaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        label = { Text("Mis reservas") },
        selected = selected,
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        }
    )
}

@Composable
fun DatePickerFieldToModal(
    viewModel: ReservationViewModel
) {
    val currentDate by viewModel.currentDate.collectAsState()
    var showModal by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = convertMillisToDate(currentDate),
        onValueChange = {},
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = null)
        },
        modifier = Modifier
            .width(160.dp)
            .pointerInput(currentDate) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showModal = true
                    }
                }
            }
    )

    if (showModal) {
        DatePickerModal(
            currentDate,
            onDateSelected = { selectedDate ->
                viewModel.updateCurrentDate(selectedDate)
                showModal = false
                focusManager.clearFocus()
            },
            onDismiss = {
                showModal = false
                focusManager.clearFocus()
            }
        )
    }

}
