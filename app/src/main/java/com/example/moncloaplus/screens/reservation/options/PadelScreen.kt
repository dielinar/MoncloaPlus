package com.example.moncloaplus.screens.reservation.options

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moncloaplus.model.User
import com.example.moncloaplus.screens.account_center.AccountCenterViewModel
import com.example.moncloaplus.model.ReservationViewModel
import com.example.moncloaplus.screens.reservation.PADEL_INDEX

@Composable
fun PadelScreen(
    resViewModel: ReservationViewModel = hiltViewModel(),
    accViewModel: AccountCenterViewModel = hiltViewModel()
) {
    val reservations by resViewModel.reservationsOfType.collectAsState()
    val userReservations by resViewModel.userReservations.collectAsState()
    val currentUser by accViewModel.user.collectAsState()

    var selected by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        resViewModel.fetchAllReservationsOfType(PADEL_INDEX)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 16.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            MyReservationsButton(
                selected = selected,
                onSelectedChange = { newValue ->
                    selected = newValue
                    if (newValue) {
                        resViewModel.fetchUserReservations(PADEL_INDEX)
                    }
                }
            )
        }

        if (selected) {
            userReservations[PADEL_INDEX]?.let {
                ReservationList(it, currentUser)
            }
        } else {
            reservations[PADEL_INDEX]?.let {
                ReservationList(it, currentUser)
            }
        }

    }

}

@Composable
fun MyReservationsButton(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit
) {
    FilterChip(
        onClick = { onSelectedChange(!selected) },
        label = { Text("Mis reservas") },
        selected = selected,
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        }
    )
}
