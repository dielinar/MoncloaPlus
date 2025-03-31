package com.example.moncloaplus.screens.reservation.options

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.moncloaplus.R
import com.example.moncloaplus.model.Reservation
import com.example.moncloaplus.model.ReservationViewModel
import com.example.moncloaplus.model.User
import com.example.moncloaplus.screens.reservation.EditReservationDialog
import com.example.moncloaplus.screens.reservation.RESERVATION_ICONS
import com.example.moncloaplus.screens.reservation.ReservationColors
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.animation.core.LinearEasing
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import com.example.moncloaplus.model.MAX_GYM_PARTICIPANTS
import com.example.moncloaplus.model.ReservType

@Composable
fun ReservationList(
    viewModel: ReservationViewModel,
    reservationsList: List<Reservation>,
    currentUser: User
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(reservationsList) { reservation ->
            ReservationCard(viewModel, reservation, currentUser)
        }
    }
}

@Composable
fun ReservationCard(
    viewModel: ReservationViewModel,
    reservation: Reservation,
    currentUser: User
) {
    val participantsForReservation = viewModel.participantsMap.collectAsState().value[reservation.id] ?: emptyList()
    val isParticipating = participantsForReservation.any { it.id == currentUser.id }
    val canParticipate = participantsForReservation.size < MAX_GYM_PARTICIPANTS
    val numberOfParticipants = participantsForReservation.size

    var showParticipantsDialog by remember { mutableStateOf(false) }

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }

    val isCurrentUser = reservation.owner?.id == currentUser.id
    val isPastReservation = reservation.inicio.toDate().time <= currentTime &&
            currentTime > reservation.final.toDate().time
    val isCurrentReservation = reservation.inicio.toDate().time <= currentTime &&
            currentTime <= reservation.final.toDate().time

    val containerColor = when {
        isPastReservation -> ReservationColors.pastContainer()
        isCurrentUser -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    val contentColor = when {
        isPastReservation -> ReservationColors.pastContent()
        else -> MaterialTheme.colorScheme.onSurface
    }

    val dividerColor = when {
        isPastReservation -> ReservationColors.pastContent()
        isCurrentUser -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPastReservation) 0.dp else 4.dp),
        shape = MaterialTheme.shapes.extraSmall,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(containerColor)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimeCard(isPastReservation, true, timeFormat.format(reservation.inicio.toDate()))
                    TimeCard(isPastReservation, false, timeFormat.format(reservation.final.toDate()))
                }
                Text(
                    text = "${reservation.owner?.firstName} ${reservation.owner?.firstSurname} ${reservation.owner?.secondSurname}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                )

                if (reservation.tipo == ReservType.GYM) {
                    Row(modifier = Modifier.fillMaxWidth().padding(start = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { showParticipantsDialog = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = contentColor),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.group_24px),
                                contentDescription = null,
                                modifier = Modifier.size(26.dp).padding(end = 6.dp),
                                tint = dividerColor
                            )
                            Text(
                                text = "${numberOfParticipants}/$MAX_GYM_PARTICIPANTS",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = dividerColor
                            )
                        }
                        if (!isCurrentUser && (isParticipating || canParticipate)) {
                            FilterChip(
                                onClick = {
                                    if (isParticipating) {
                                        viewModel.deleteParticipant(reservation, currentUser.id)
                                    } else {
                                        viewModel.addParticipant(reservation, currentUser.id)
                                    }
                                },
                                label = { Text(if (isParticipating) "Te has unido" else "Unirme") },
                                selected = isParticipating,
                                enabled = !isCurrentReservation && !isPastReservation,
                                modifier = Modifier.scale(0.7f).offset(x = (-16).dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f),
                                    selectedLabelColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLeadingIconColor = MaterialTheme.colorScheme.primaryContainer
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
                    }
                }
                if (reservation.nota.isNotBlank()) {
                    HorizontalDivider(color = dividerColor.copy(0.3f), thickness = 1.dp)
                    NoteLabel(reservation.nota)
                }
            }

            if ((!isPastReservation && !isCurrentReservation && isCurrentUser) || (currentUser.isAdmin()))
                MenuOptions(
                    currentUser = currentUser,
                    modifier = Modifier.align(Alignment.TopEnd),
                    reservation = reservation,
                    viewModel = viewModel,
                    moreOptionsColor = dividerColor
                )
            if (isCurrentReservation) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BlinkingLiveIcon()
                    Text(
                        "ahora",
                        color = BadgeDefaults.containerColor,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    )
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

    if (showParticipantsDialog) {
        AlertDialog(
            onDismissRequest = { showParticipantsDialog = false },
            icon = { Icon(painterResource(R.drawable.group_24px), null) },
            title = { Text("Participantes", textAlign = TextAlign.Center) },
            text = {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))
                    participantsForReservation.forEach { user ->
                        Row(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                            Icon(
                                painter = painterResource(if (reservation.owner!!.id == user.id) R.drawable.shield_person_24px else R.drawable.person_24px),
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = if (user.id == currentUser.id) "Yo" else "${user.firstName} ${user.firstSurname} ${user.secondSurname}",
                                fontWeight = if (reservation.owner!!.id == user.id) FontWeight.Bold else FontWeight.Normal,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showParticipantsDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
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
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun NoteLabel(text: String) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
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
    currentUser: User,
    modifier: Modifier,
    reservation: Reservation,
    viewModel: ReservationViewModel,
    moreOptionsColor: Color
) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, "More options", tint = moreOptionsColor)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Editar") },
                leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = "Editar") },
                onClick = {
                    if (currentUser.isAdmin()) viewModel.adminLoadReservationForEditing(reservation.owner!!.id, reservation.id)
                    else viewModel.loadReservationForEditing(reservation.id)
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
            text = { Text("¿Estás seguro de eliminar la reserva?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (currentUser.isAdmin()) viewModel.adminDelete(reservation)
                            else viewModel.deleteReservation(reservation)
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
                title = { Text("Cargando reserva", textAlign = TextAlign.Center) },
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
                    if (currentUser.isAdmin()) viewModel.adminEdit()
                        else viewModel.editReservation()
                    showEditDialog = false
                }
            )
        }
    }

}

@Composable
fun BlinkingLiveIcon() {
    val alpha by rememberInfiniteTransition(label = "Live reservation").animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "Live reservation"
    )

    Icon(
        painter = painterResource(R.drawable.sensors_24px),
        contentDescription = null,
        tint = BadgeDefaults.containerColor,
        modifier = Modifier
            .size(28.dp)
            .padding(end = 6.dp)
            .alpha(alpha)
    )
}
