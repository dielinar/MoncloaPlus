package com.example.moncloaplus.screens.reservation.options

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moncloaplus.screens.reservation.NewReservationButton
import com.example.moncloaplus.screens.reservation.ReservationViewModel
import com.example.moncloaplus.screens.authentication.AccountCenterViewModel
import com.example.moncloaplus.screens.reservation.DatePickerFieldToModal
import com.example.moncloaplus.screens.reservation.LoadingIndicator
import com.example.moncloaplus.screens.reservation.MyReservationsButton
import com.example.moncloaplus.screens.reservation.PIANO_INDEX
import com.example.moncloaplus.screens.reservation.normalizeDate

@Composable
fun PianoScreen(
    resViewModel: ReservationViewModel = hiltViewModel(),
    accViewModel: AccountCenterViewModel = hiltViewModel()
) {
    val reservationsByDate by resViewModel.reservationsByDate.collectAsState()
    val userReservations by resViewModel.userReservations.collectAsState()
    val currentUser by accViewModel.user.collectAsState()
    val currentDate by resViewModel.currentDate.collectAsState()
    val isLoading by resViewModel.isLoading.collectAsState()

    var selected by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        resViewModel.fetchReservationsByDate(PIANO_INDEX, currentDate)
    }

    Scaffold(
        floatingActionButton = {
            NewReservationButton(PIANO_INDEX, resViewModel)
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                DatePickerFieldToModal(
                    PIANO_INDEX,
                    currentDate,
                    onDateSelected = { type, selectedDate -> resViewModel.updateCurrentDate(type, selectedDate) }
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier.fillMaxWidth().padding(end = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                MyReservationsButton(
                    selected = selected,
                    onSelectedChange = { newValue ->
                        selected = newValue
                        if (newValue) resViewModel.fetchUserReservations(PIANO_INDEX, currentDate)
                    }
                )
            }

            if (isLoading) {
                LoadingIndicator()
            } else {
                val normalizedCurrentDate = normalizeDate(currentDate)
                val reservationsToShow = if (selected) {
                    userReservations[PIANO_INDEX]?.get(normalizedCurrentDate)
                } else {
                    reservationsByDate[PIANO_INDEX]?.get(normalizedCurrentDate)
                }

                if (reservationsToShow == null) {
                    // No mostrar nada si aún no están listas las reservas (evita el mensaje falso)
                } else if (reservationsToShow.isEmpty()) {
                    Text(
                        text = if (selected) "No tienes reservas para este día." else "Sin reservas para este día.",
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic
                    )
                } else {
                    ReservationList(resViewModel, reservationsToShow, currentUser)
                }
            }

        }
    }
}
