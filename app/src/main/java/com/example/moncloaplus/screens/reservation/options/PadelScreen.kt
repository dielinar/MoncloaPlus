package com.example.moncloaplus.screens.reservation.options

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moncloaplus.model.ReservType
import com.example.moncloaplus.screens.reservation.NewReservationButton
import com.example.moncloaplus.screens.reservation.ReservationDialog
import com.example.moncloaplus.screens.reservation.ReservationViewModel
import com.example.moncloaplus.utils.PADEL_INDEX

@Composable
fun PadelScreen(
    viewModel: ReservationViewModel = hiltViewModel()
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        NewReservationButton(PADEL_INDEX, viewModel)
    }

}
