package com.example.moncloaplus.screens.reservation.options

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.moncloaplus.R
import com.example.moncloaplus.model.Reservation
import com.example.moncloaplus.model.User
import com.example.moncloaplus.screens.reservation.ReservationColors
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ReservationList(
    reservationsList: List<Reservation>,
    currentUser: User,
    onDelete: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(reservationsList) { reservation ->
            ReservationCard(reservation, currentUser, onDelete)
        }
    }
}

@Composable
fun ReservationCard(
    reservation: Reservation,
    currentUser: User,
    onDelete: (String) -> Unit
) {

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val isCurrentUser = reservation.owner?.id == currentUser.id

    val containerColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    val dividerColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimeCard(timeFormat.format(reservation.inicio.toDate()), ReservationColors.greenContent, ReservationColors.greenContainer)
                    TimeCard(timeFormat.format(reservation.final.toDate()), ReservationColors.redContent, ReservationColors.redContainer)
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

            if (isCurrentUser)
                MenuOptions(
                    modifier = Modifier.align(Alignment.TopEnd),
                    reservationId = reservation.id,
                    onDelete = onDelete
                )

        }
    }


}

@Composable
fun TimeCard(text: String, contentColor: Color, containerColor: Color) {
    Card(
        colors = CardDefaults.cardColors(
            contentColor = contentColor,
            containerColor = containerColor
        ),
        border = BorderStroke(2.dp, contentColor)
    ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center)
        {
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
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
    onDelete: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

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
                    /* Do something... */
                }
            )
            DropdownMenuItem(
                text = { Text("Eliminar") },
                leadingIcon = { Icon(painterResource(R.drawable.delete_24px), "Eliminar")},
                onClick = {
                    expanded = false
                    showDialog = true
                }
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = { Icon(painterResource(R.drawable.delete_24px), null) },
            title = { Text("Eliminar reserva") },
            text = { Text("¿Estás seguro de eliminar tu reserva?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(reservationId)
                        showDialog = false
                    }
                ) { Text("Sí, eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Atrás")
                }
            }
        )
    }

}
