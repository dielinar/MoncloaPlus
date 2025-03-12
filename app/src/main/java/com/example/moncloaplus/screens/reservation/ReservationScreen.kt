package com.example.moncloaplus.screens.reservation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.moncloaplus.model.ReservationViewModel
import com.example.moncloaplus.screens.reservation.options.GymScreen
import com.example.moncloaplus.screens.reservation.options.MusicStudioScreen
import com.example.moncloaplus.screens.reservation.options.PadelScreen
import com.example.moncloaplus.screens.reservation.options.PianoScreen

@Composable
fun ReservationScreen(
    navController: NavHostController,
    viewModel: ReservationViewModel = hiltViewModel()
) {
    val currentBackStackEntry by navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry)
    val currentRoute = currentBackStackEntry?.destination?.route

    val selectedIndex = RESERVATION_ROUTES.indexOf(currentRoute).coerceAtLeast(0)

    Scaffold(
        bottomBar = {
            NavigationBar {
                RESERVATION_NAMES.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(painter = painterResource(RESERVATION_ICONS[index]),null) },
                        label = { Text(text = item) },
                        selected = currentRoute == RESERVATION_ROUTES[index],
                        onClick = { navController.navigate(RESERVATION_ROUTES[index]) }
                    )
                }
            }
        },
        floatingActionButton = {
            NewReservationButton(selectedIndex, viewModel)
        },
        floatingActionButtonPosition = FabPosition.Start
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = PADEL_SCREEN
            ) {
                reservationGraph()
            }
        }
    }

}

fun NavGraphBuilder.reservationGraph() {

    composable(PADEL_SCREEN) {
        PadelScreen()
    }

    composable(GYM_SCREEN) {
        GymScreen()
    }

    composable(MUSIC_STUDIO_SCREEN) {
        MusicStudioScreen()
    }

    composable(PIANO_SCREEN) {
        PianoScreen()
    }

}
