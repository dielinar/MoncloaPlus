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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moncloaplus.model.Reservation
import com.example.moncloaplus.model.User
import com.example.moncloaplus.screens.account_center.AccountCenterViewModel
import com.example.moncloaplus.screens.reservation.NewReservationButton
import com.example.moncloaplus.model.ReservationViewModel
import com.example.moncloaplus.screens.reservation.PADEL_INDEX
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PadelScreen(
    resViewModel: ReservationViewModel = hiltViewModel(),
    accViewModel: AccountCenterViewModel = hiltViewModel()
) {

    val reservations by resViewModel.reservationsOfType.collectAsState()
    val userReservations by resViewModel.userReservations.collectAsState()
    val user by accViewModel.user.collectAsState(initial = User())

    LaunchedEffect(Unit) {
        resViewModel.fetchAllReservationsOfType(PADEL_INDEX)
    }

    Column(modifier = Modifier.fillMaxSize())
    {
        reservations[PADEL_INDEX]?.let { ReservationList(it) }
    }

}
