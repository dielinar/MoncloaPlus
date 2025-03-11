package com.example.moncloaplus.screens.reservation.options

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moncloaplus.screens.reservation.NewReservationButton
import com.example.moncloaplus.screens.reservation.ReservationViewModel
import com.example.moncloaplus.utils.MUSIC_STUDIO_INDEX

@Composable
fun MusicStudioScreen(
    viewModel: ReservationViewModel = hiltViewModel()

) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        NewReservationButton(MUSIC_STUDIO_INDEX, viewModel)
    }

}
