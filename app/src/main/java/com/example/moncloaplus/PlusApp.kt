package com.example.moncloaplus

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moncloaplus.screens.account_center.AccountCenterScreen
import com.example.moncloaplus.screens.authentication.sign_in.SignInScreen
import com.example.moncloaplus.screens.authentication.sign_up.SignUpScreen
import com.example.moncloaplus.screens.MainScreen
import com.example.moncloaplus.screens.admin.AdminScreen
import com.example.moncloaplus.screens.splash.SplashScreen
import com.example.moncloaplus.screens.user_data.UserDataScreen
import com.example.moncloaplus.ui.theme.PlusTheme
import kotlinx.coroutines.CoroutineScope

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlusApp() {
    PlusTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val snackbarHostState = remember { SnackbarHostState() }
            val appState = rememberAppState(snackbarHostState)

            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
            ) { innerPaddingModifier ->
                NavHost(
                    navController = appState.navController,
                    startDestination = SIGN_IN_SCREEN,
                    modifier = Modifier.padding(innerPaddingModifier)
                ) {
                    plusGraph(appState)
                }
            }
        }
    }
}

@Composable
fun rememberAppState(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController = rememberNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): PlusAppState {
    return remember(snackbarHostState, navController, snackbarManager, coroutineScope) {
        PlusAppState(snackbarHostState, navController, snackbarManager, coroutineScope)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.plusGraph(appState: PlusAppState) {
    composable(SIGN_IN_SCREEN) {
        SignInScreen(
            openScreen = { route -> appState.navigate(route) },
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) }
        )
    }

    composable(SIGN_UP_SCREEN) {
        SignUpScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable(SPLASH_SCREEN) {
        SplashScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable(ACCOUNT_CENTER_SCREEN) {
        AccountCenterScreen(restartApp = { route -> appState.clearAndNavigate(route) })
    }

    composable(USER_DATA_SCREEN) {
        UserDataScreen(openAndPopUp = {route, popUp -> appState.navigateAndPopUp(route, popUp)})
    }

    composable(MAIN_SCREEN) {
        val navController = rememberNavController()
        MainScreen(
            restartApp = { route -> appState.clearAndNavigate(route) },
            navController = navController
        )
    }

    composable(ADMIN_SCREEN) {
        val navController = rememberNavController()
        AdminScreen(
            restartApp = { route -> appState.clearAndNavigate(route) },
            navController = navController
        )
    }

}