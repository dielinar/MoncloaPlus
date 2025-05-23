package com.example.moncloaplus.screens.reservation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.moncloaplus.screens.reservation.options.GymScreen
import com.example.moncloaplus.screens.reservation.options.MusicStudioScreen
import com.example.moncloaplus.screens.reservation.options.PadelScreen
import com.example.moncloaplus.screens.reservation.options.PianoScreen

@Composable
fun ReservationScreen(
    navController: NavHostController
) {
    val currentRoute by navController.currentBackStackEntryFlow
        .collectAsState(initial = navController.currentBackStackEntry)

    Scaffold(
        bottomBar = {
            NavigationBar {
                RESERVATION_NAMES.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(painter = painterResource(RESERVATION_ICONS[index]),null) },
                        label = { Text(text = item) },
                        selected = currentRoute?.destination?.route == RESERVATION_ROUTES[index],
                        onClick = { navController.navigate(RESERVATION_ROUTES[index]) }
                    )
                }
            }
        }
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

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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
