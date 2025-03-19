package com.example.moncloaplus.screens.reservation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
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
import kotlinx.coroutines.delay

@Composable
fun ReservationScreen(
    navController: NavHostController,
    resViewModel: ReservationViewModel = hiltViewModel()
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
