package com.example.moncloaplus.screens.reservation

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moncloaplus.R
import com.example.moncloaplus.utils.formatHourMinute
import com.example.moncloaplus.utils.toFormattedDate

@Composable
fun ReservationScreen(
    modifier: Modifier = Modifier,
    viewModel: ReservationViewModel = hiltViewModel()
) {

    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(start = 14.dp, top = 14.dp),
                onClick = { showDialog = true },
                icon = { Icon(Icons.Filled.Add, null) },
                text = { Text("Nueva reserva") }
            )
        }

        Spacer(Modifier.weight(1f))
        ReservationOptionsNavigationBar()
    }

    if (showDialog) {
        ReservationDialog(
            viewModel,
            onDismiss = { showDialog = false },
            onConfirm = { showDialog = false }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDialog(
    viewModel: ReservationViewModel,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    val note by viewModel.note.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    val selectedDate by viewModel.selectedDate.collectAsState()

    val (defaultHour, defaultMinute) = viewModel.getDefaultStartTime()
    val startTime by viewModel.startTime.collectAsState()
    if (startTime == null) {
        viewModel.updateStartTime(defaultHour, defaultMinute)
    }

    val (defaultEndHour, defaultEndMinute) = viewModel.getDefaultEndTime(defaultHour, defaultMinute)
    val endTime by viewModel.endTime.collectAsState()
    if (endTime == null) {
        viewModel.updateEndTime(defaultEndHour, defaultEndMinute)
    }

    val currentDate = remember { System.currentTimeMillis() }
    if (selectedDate == null) {
        viewModel.updateSelectedDate(currentDate)
    }

    Dialog(onDismissRequest = onDismiss) {
        val focusManager = LocalFocusManager.current

        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp
        ) {
            Column (
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Icon(
                    painter = painterResource(R.drawable.sports_tennis_24px),
                    tint = MaterialTheme.colorScheme.tertiary,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Nueva reserva de pádel",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider(modifier = Modifier.padding(top = 30.dp, bottom = 6.dp))

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        "Selecciona fecha:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    TextButton(
                        onClick = {showDatePicker = true}
                    ) {
                        Text(text = selectedDate?.toFormattedDate() ?: "Selecciona fecha")
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(top = 6.dp, bottom = 6.dp))

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        "Hora de inicio:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    TextButton(onClick = {showStartTimePicker = true}) {
                        Text(text = startTime?.let { formatHourMinute(it.first, it.second) } ?: "Hora de inicio")
                    }
                }

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        "Hora de fin:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    TextButton(onClick = {showEndTimePicker = true}) {
                        Text(text = endTime?.let { formatHourMinute(it.first, it.second) } ?: "Hora de fin")
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(top = 6.dp, bottom = 6.dp).fillMaxWidth())

                OutlinedTextField(
                    value = note,
                    onValueChange = { viewModel.updateNote(it) },
                    label = { Text("Nota", style = MaterialTheme.typography.labelMedium) },
                    modifier = Modifier.fillMaxWidth().focusable(true),
                    maxLines = 3,
                    singleLine = false,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancelar", color = MaterialTheme.colorScheme.tertiary) }
                    TextButton(onClick = onConfirm) { Text("Guardar", color = MaterialTheme.colorScheme.tertiary) }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { date ->
                viewModel.updateSelectedDate(date)
                showDatePicker = false },
            onDismiss = { showDatePicker = false}
        )
    }

    if (showStartTimePicker) {
        AdvancedTimePicker(
            onConfirm = { state ->
                viewModel.updateStartTime(state.hour, state.minute)
                showStartTimePicker = false },
            onDismiss = { showStartTimePicker = false}
        )
    }

    if (showEndTimePicker) {
        AdvancedTimePicker(
            onConfirm = { state ->
                viewModel.updateEndTime(state.hour, state.minute)
                showEndTimePicker = false
            },
            onDismiss = { showEndTimePicker = false }
        )
    }

}
