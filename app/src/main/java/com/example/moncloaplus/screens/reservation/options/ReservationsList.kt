package com.example.moncloaplus.screens.reservation.options

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moncloaplus.model.Reservation
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ReservationList(reservations: List<Reservation>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(reservations) { reservation ->
            ReservationCard(reservation)
        }
    }
}

@Composable
fun ReservationCard(reservation: Reservation) {

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Reserva ID: ${reservation.id}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Inicio: ${timeFormat.format(reservation.inicio.toDate())}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Final: ${timeFormat.format(reservation.final.toDate())}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Nota: ${reservation.nota}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Tipo: ${reservation.tipo}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}