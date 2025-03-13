package com.example.moncloaplus.screens.reservation.options

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moncloaplus.screens.reservation.NewReservationButton
import com.example.moncloaplus.model.ReservationViewModel
import com.example.moncloaplus.model.User
import com.example.moncloaplus.screens.account_center.AccountCenterViewModel
import com.example.moncloaplus.screens.reservation.MUSIC_STUDIO_INDEX
import com.example.moncloaplus.screens.reservation.PADEL_INDEX

@Composable
fun MusicStudioScreen(
    resViewModel: ReservationViewModel = hiltViewModel(),
    accViewModel: AccountCenterViewModel = hiltViewModel()
) {

    val reservations by resViewModel.reservationsOfType.collectAsState()
    val currentUser by accViewModel.user.collectAsState()

    LaunchedEffect(Unit) {
        resViewModel.fetchAllReservationsOfType(MUSIC_STUDIO_INDEX)
    }

    Column(modifier = Modifier.fillMaxSize())
    {
    }

}
