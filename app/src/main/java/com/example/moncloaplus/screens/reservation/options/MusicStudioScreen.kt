package com.example.moncloaplus.screens.reservation.options

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moncloaplus.screens.reservation.NewReservationButton
import com.example.moncloaplus.model.ReservationViewModel
import com.example.moncloaplus.screens.reservation.MUSIC_STUDIO_INDEX
import com.example.moncloaplus.screens.reservation.PADEL_INDEX

@Composable
fun MusicStudioScreen(
    resViewModel: ReservationViewModel = hiltViewModel()
) {

    val reservations by resViewModel.reservationsOfType.collectAsState()

    LaunchedEffect(Unit) {
        resViewModel.fetchAllReservationsOfType(MUSIC_STUDIO_INDEX)
    }

    Column(modifier = Modifier.fillMaxSize())
    {
        reservations[MUSIC_STUDIO_INDEX]?.let { ReservationList(it) }
    }

}
