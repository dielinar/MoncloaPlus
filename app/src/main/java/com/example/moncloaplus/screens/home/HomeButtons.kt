package com.example.moncloaplus.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.moncloaplus.model.Event
import com.example.moncloaplus.model.EventViewModel
import com.example.moncloaplus.screens.reservation.convertMillisToDate
import com.example.moncloaplus.screens.reservation.formatHourMinute
import java.util.Calendar

@Composable
fun ImageCarousel(
    events: List<Event>,
    viewModel: EventViewModel,
    currentUserId: String
) {
    var showImageDialog by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf("") }

    var showInfoDialog by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(events) { event ->

            val isParticipating by remember(event.asistentes) {
                derivedStateOf { currentUserId in event.asistentes }

            }
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .width(215.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = event.subtipo,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

                    AsyncImage(
                        model = event.cartel.url,
                        contentDescription = event.titulo,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clickable {
                                selectedImageUrl = event.cartel.url
                                showImageDialog = true
                            }
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(start = 8.dp, top = 4.dp, bottom = 4.dp, end = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ParticipateButton(
                            isParticipating = isParticipating,
                            onToggleParticipate = { participate ->
                                if (participate) {
                                    viewModel.addParticipant(event.id)
                                } else {
                                    viewModel.removeParticipant(event.id)
                                }
                            }
                        )

                        IconButton(
                            onClick = {
                                selectedEvent = event
                                showInfoDialog = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = "Info",
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

            }
        }
    }

    if (showImageDialog) {
        Dialog(onDismissRequest = { showImageDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showImageDialog = false },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = selectedImageUrl,
                    contentDescription = "Imagen ampliada",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }

    if (showInfoDialog && selectedEvent != null) {
        Dialog(onDismissRequest = { showInfoDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp, vertical = 24.dp)
                    ) {
                        Column {
                            AsyncImage(
                                model = selectedEvent!!.cartel.url,
                                contentDescription = "Cartel del evento",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(360.dp),
                                contentScale = ContentScale.Fit
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = selectedEvent!!.titulo,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            val eventMillis = selectedEvent!!.fecha.toDate().time
                            val formattedDate = convertMillisToDate(eventMillis)
                            Text(
                                text = "Fecha: $formattedDate",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (selectedEvent!!.allDay) {
                                Text(
                                    text = "Todo el día",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            } else {
                                val calendar = Calendar.getInstance().apply { timeInMillis = eventMillis }
                                val formattedTime = formatHourMinute(
                                    hour = calendar.get(Calendar.HOUR_OF_DAY),
                                    minute = calendar.get(Calendar.MINUTE)
                                )
                                Text(
                                    text = "Hora: $formattedTime",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (selectedEvent!!.ponentes.isNotEmpty()) {
                                Text(
                                    text = "Ponentes: ${selectedEvent!!.ponentes.joinToString(", ")}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = selectedEvent!!.descripcion,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 24.dp, bottom = 24.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showInfoDialog = false }
                        ) {
                            Text("Cerrar")
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun ParticipateButton(
    isParticipating: Boolean,
    onToggleParticipate: (Boolean) -> Unit
) {
    val borderColor = if (isParticipating) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.outline

    FilterChip(
        onClick = { onToggleParticipate(!isParticipating) },
        label = {
            Text(
                text = if (isParticipating) "Apuntado" else "Apuntarse",
                color = borderColor
            )
        },
        selected = isParticipating,
        enabled = true,
        modifier = Modifier.scale(0.9f),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        leadingIcon = if (isParticipating) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "DoneIcon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else null
    )
}
