package com.example.moncloaplus.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moncloaplus.model.EventType
import com.example.moncloaplus.model.EventViewModel
import com.example.moncloaplus.screens.create_event.eventTypeNameMap

@Composable
fun HomeScreen(
    viewModel: EventViewModel = hiltViewModel()
) {
    val allEvents by viewModel.allEvents.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAllEvents()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        allEvents.forEach{ (eventTypeOrdinal, events) ->
            val eventType = EventType.entries[eventTypeOrdinal]
            val title = eventTypeNameMap[eventType] ?: "Otros"

            item {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )

                    val eventsWithImage = events.filter { it.cartel.url.isNotEmpty() }

                    if (eventsWithImage.isNotEmpty()) {
                        ImageCarousel(events = eventsWithImage)
                    } else {
                        Text(
                            text = "No hay eventos disponibles",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }

}
