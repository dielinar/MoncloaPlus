package com.example.moncloaplus.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moncloaplus.data.model.EventType
import com.example.moncloaplus.screens.create_event.EventViewModel
import com.example.moncloaplus.screens.user_data.UserViewModel
import com.example.moncloaplus.screens.authentication.AccountCenterViewModel
import com.example.moncloaplus.screens.create_event.eventTypeNameMap
import com.example.moncloaplus.screens.reservation.LoadingIndicator

@Composable
fun HomeScreen(
    eventViewModel: EventViewModel = hiltViewModel(),
    accViewModel: AccountCenterViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val allEvents by eventViewModel.allEvents.collectAsState()
    val isLoading by eventViewModel.isLoading.collectAsState()
    val currentUser by accViewModel.user.collectAsState()

    LaunchedEffect(Unit) {
        eventViewModel.fetchAllEvents()
    }

    if (isLoading) {
        LoadingIndicator()
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            allEvents.forEach{ (eventTypeOrdinal, events) ->
                val eventType = EventType.entries[eventTypeOrdinal]
                val title = eventTypeNameMap[eventType] ?: "Otros"

                item {
                    Column {
                        Column {
                            HorizontalDivider(color = MaterialTheme.colorScheme.onTertiaryContainer, thickness = 1.dp)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.onTertiaryContainer, thickness = 1.dp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val eventsWithImage = events.filter { it.cartel.url.isNotEmpty() }

                        if (eventsWithImage.isNotEmpty()) {
                            ImageCarousel(
                                events = eventsWithImage,
                                eventViewModel = eventViewModel,
                                userViewModel = userViewModel,
                                currentUser = currentUser
                            )
                        } else {
                            Text(
                                text = "No hay eventos disponibles.",
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = FontStyle.Italic,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

}
