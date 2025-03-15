package com.example.moncloaplus.screens.reservation.options

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.moncloaplus.screens.account_center.AccountCenterViewModel
import com.example.moncloaplus.model.ReservationViewModel
import com.example.moncloaplus.screens.reservation.DatePickerFieldToModal
import com.example.moncloaplus.screens.reservation.MyReservationsButton
import com.example.moncloaplus.screens.reservation.NewReservationButton
import com.example.moncloaplus.screens.reservation.PADEL_INDEX

@Composable
fun PadelScreen(
    resViewModel: ReservationViewModel = hiltViewModel(),
    accViewModel: AccountCenterViewModel = hiltViewModel()
) {
    val reservationsOnDay by resViewModel.reservationsOnDay.collectAsState()
    val userReservations by resViewModel.userReservations.collectAsState()
    val currentUser by accViewModel.user.collectAsState()
    val currentDate by resViewModel.currentDate.collectAsState()

    var selected by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        resViewModel.fetchReservationsOnDay(PADEL_INDEX, currentDate)
    }

    Scaffold(
        floatingActionButton = {
            NewReservationButton(PADEL_INDEX, resViewModel)
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 16.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DatePickerFieldToModal(
                    PADEL_INDEX,
                    currentDate,
                    onDateSelected = { type, selectedDate ->
                        resViewModel.updateCurrentDate(type, selectedDate)
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                MyReservationsButton(
                    selected = selected,
                    onSelectedChange = { newValue ->
                        selected = newValue
                        if (newValue) {
                            resViewModel.fetchUserReservations(PADEL_INDEX, currentDate)
                        }
                    }
                )
            }

            val reservationsToShow = if (selected) userReservations[PADEL_INDEX] else reservationsOnDay
            if (reservationsToShow.isNullOrEmpty()) {
                Text(
                    text = if (selected) "No tienes reservas para este día." else "Sin reservas para este día.",
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic
                )
            } else {
                ReservationList(reservationsToShow, currentUser, onDelete = { resId -> resViewModel.deleteReservation(resId) })
            }

        }
    }

}
