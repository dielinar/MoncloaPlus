package com.example.moncloaplus.screens.create_event

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.moncloaplus.R
import com.example.moncloaplus.model.EventViewModel

@Composable
fun CreateEventScreen(
    viewModel: EventViewModel = hiltViewModel()
) {
    val title by viewModel.title.collectAsState()
    val eventType by viewModel.eventType.collectAsState()
    val description by viewModel.description.collectAsState()
    val speakers by viewModel.speakers.collectAsState()
    val imageUri by viewModel.imageUri.collectAsState()
    val date by viewModel.date.collectAsState()
    val eventTime by viewModel.eventTime.collectAsState()

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()

    var isAllDay by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.updateImageUri(it) }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(bottom = 48.dp)) {

        // Save button
        Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp, end = 24.dp)) {
            SaveEventButton(
                onSave = {},
//                onSave = {
//                    val finalTime = if (isAllDay) null else eventTime
//                    viewModel.saveEvent(
//                        type = selectedType,
//                        isAllDay = isAllDay,
//                        time = finalTime
//                    )
//                }
                enabled = true,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Add a title
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
            BasicTextField(
                value = title,
                onValueChange = { viewModel.updateTitle(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusable(true)
                    .focusRequester(focusRequester),
                textStyle = MaterialTheme.typography.headlineMedium.copy(
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
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (title.isEmpty()) {
                            Text(
                                text = "Añade un título",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Event type selector
        Box(modifier = Modifier.fillMaxWidth()) {
            EventTypeSelector(
                selectedType = eventType,
                onTypeSelected = { viewModel.updateEventType(it) }
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        // All day option
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
            AllDayOption(
                isAllDay = isAllDay,
                onAllDayChange = { isAllDay = it }
            )
        }

        // Date and time pickers
        Row(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 14.dp)) {
            SelectDatePicker(
                selectedDate = date,
                onDateSelected = { viewModel.updateDate(it) }
            )
            Spacer(modifier = Modifier.weight(1f))
            if (!isAllDay) {
                SelectTimePicker(
                    selectedTime = eventTime,
                    onTimeSelected = { viewModel.updateEventTime(it) }
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        // Speakers text fields
        MultipleSpeakersField(
            speakers = speakers,
            onSpeakersChange = { viewModel.updateSpeakers(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 18.dp))

        // Description text field
        LabeledTextField(
            value = description,
            onValueChange = { viewModel.updateDescription(it) },
            placeholder = "Añade una descripción",
            iconResId = R.drawable.notes_24px,
            contentDescription = "Descripción",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 18.dp))

        // Poster selector
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text(text = if (imageUri == null) "Seleccionar cartel" else "Cambiar cartel")
                }

                imageUri?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Cartel del evento",
                        modifier = Modifier
                            .width(200.dp)
                            .height(300.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                    )
                }
            }
        }

    }

}
