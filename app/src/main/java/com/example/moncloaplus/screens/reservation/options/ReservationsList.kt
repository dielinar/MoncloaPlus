package com.example.moncloaplus.screens.reservation.options

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BadgeDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.moncloaplus.R
import com.example.moncloaplus.model.Reservation
import com.example.moncloaplus.model.ReservationViewModel
import com.example.moncloaplus.model.User
import com.example.moncloaplus.screens.reservation.EditReservationDialog
import com.example.moncloaplus.screens.reservation.RESERVATION_ICONS
import com.example.moncloaplus.screens.reservation.ReservationColors
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ReservationList(
    viewModel: ReservationViewModel,
    reservationsList: List<Reservation>,
    currentUser: User,
    onDelete: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(reservationsList) { reservation ->
            ReservationCard(viewModel, reservation, currentUser, onDelete)
        }
    }
}

@Composable
fun ReservationCard(
    viewModel: ReservationViewModel,
    reservation: Reservation,
    currentUser: User,
    onDelete: (String) -> Unit
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val now = System.currentTimeMillis()

    val isCurrentUser = reservation.owner?.id == currentUser.id
    val isPastReservation = reservation.inicio.toDate().time <= now && now > reservation.final.toDate().time
    val isCurrentReservation = reservation.inicio.toDate().time <= now && now <= reservation.final.toDate().time

    val containerColor = when {
        isPastReservation -> ReservationColors.pastContainer()
        isCurrentUser -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    val contentColor = when {
        isPastReservation -> ReservationColors.pastContent()
        isCurrentUser -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    val dividerColor = when {
        isPastReservation -> ReservationColors.pastContent()
        isCurrentUser -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    val durationText = getDurationText(
        reservation.inicio.toDate().time,
        reservation.final.toDate().time
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.extraSmall,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth().background(containerColor)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimeCard(
                        isPastReservation,
                        true,
                        timeFormat.format(reservation.inicio.toDate())
                    )
                    TimeCard(
                        isPastReservation,
                        false,
                        timeFormat.format(reservation.final.toDate())
                    )
                    Text(durationText, style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic)
                }
                Text(
                    text = "${reservation.owner?.firstName} ${reservation.owner?.firstSurname} ${reservation.owner?.secondSurname}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, bottom = 24.dp)
                )
                if (reservation.nota.isNotBlank()) {
                    HorizontalDivider(color = dividerColor, thickness = Dp.Hairline)
                    NoteLabel(reservation.nota)
                }
            }

            if (!isPastReservation && !isCurrentReservation && isCurrentUser)
                MenuOptions(
                    modifier = Modifier.align(Alignment.TopEnd),
                    reservationId = reservation.id,
                    viewModel = viewModel,
                    onDelete = onDelete
                )
            if (isCurrentReservation) {
                Row(modifier = Modifier.padding(12.dp).align(Alignment.TopEnd), verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.sensors_24px), null, tint = BadgeDefaults.containerColor, modifier = Modifier.size(28.dp).padding(end = 6.dp))
                    Text("en curso", color = BadgeDefaults.containerColor, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
                }
            }

            Icon(
                painter = painterResource(RESERVATION_ICONS[reservation.tipo.ordinal]),
                contentDescription = "Reservation icon",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 15.dp, y = 15.dp)
                    .size(80.dp)
                    .alpha(0.15f),
                tint = contentColor
            )
        }
    }

}

@Composable
fun TimeCard(
    isPastReservation: Boolean,
    isStartTime: Boolean,
    text: String
) {
    val containerColor = if (isPastReservation) ReservationColors.pastContainer()
    else
        if (isStartTime) ReservationColors.greenContainer
        else ReservationColors.redContainer

    val contentColor = if (isPastReservation) ReservationColors.pastContent()
    else
        if (isStartTime) ReservationColors.greenContent
        else ReservationColors.redContent

    Card(
        colors = CardDefaults.cardColors(
            contentColor = contentColor,
            containerColor = containerColor,
        ),
        border = BorderStroke(1.dp, contentColor),
        shape = MaterialTheme.shapes.small
        ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center)
        {
            Text(
                text = text,
                style = if (isStartTime) MaterialTheme.typography.titleMedium
                        else MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun getDurationText(startTime: Long, endTime: Long): String {
    val durationMillis = endTime - startTime
    val totalMinutes = (durationMillis / (1000 * 60)).toInt()
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return when {
        hours > 0 && minutes > 0 -> "$hours hora${if (hours > 1) "s" else ""} y $minutes minuto${if (minutes > 1) "s" else ""}"
        hours > 0 -> "$hours hora${if (hours > 1) "s" else ""}"
        else -> "$minutes minuto${if (minutes > 1) "s" else ""}"
    }
}

@Composable
fun NoteLabel(text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun MenuOptions(
    modifier: Modifier,
    reservationId: String,
    viewModel: ReservationViewModel,
    onDelete: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Editar") },
                leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = "Editar") },
                onClick = {
                    viewModel.loadReservationForEditing(reservationId)
                    expanded = false
                    showEditDialog = true
                }
            )
            DropdownMenuItem(
                text = { Text("Eliminar") },
                leadingIcon = { Icon(painterResource(R.drawable.delete_24px), "Eliminar")},
                onClick = {
                    expanded = false
                    showDeleteDialog = true
                }
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(painterResource(R.drawable.delete_24px), null) },
            title = { Text("Eliminar reserva") },
            text = { Text("¿Estás seguro de eliminar tu reserva?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(reservationId)
                        showDeleteDialog = false
                    }
                ) { Text("Sí, eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Atrás")
                }
            }
        )
    }

    if (showEditDialog) {
        val editingReservation by viewModel.editingReservation.collectAsState()

        if (editingReservation == null) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Cargando reserva") },
                text = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        } else {
            EditReservationDialog(
                viewModel = viewModel,
                onDismiss = {
                    viewModel.resetValues()
                    showEditDialog = false
                },
                onConfirm = {
                    viewModel.editReservation()
                    showEditDialog = false
                }
            )
        }
    }

}
