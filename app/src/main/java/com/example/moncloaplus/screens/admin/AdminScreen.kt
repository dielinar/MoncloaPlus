package com.example.moncloaplus.screens.admin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.moncloaplus.HOME_SCREEN
import com.example.moncloaplus.R
import com.example.moncloaplus.screens.AppBar
import com.example.moncloaplus.screens.account_center.AccountCenterViewModel
import com.example.moncloaplus.screens.admin.export_meals.ExportMealsScreen
import com.example.moncloaplus.screens.home.EXPORT_MEALS_SCREEN
import com.example.moncloaplus.screens.home.HomeScreen
import com.example.moncloaplus.screens.home.USER_SEARCH_SCREEN
import com.example.moncloaplus.screens.user_search.UserSearchScreen
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdminScreen(
    restartApp: (String) -> Unit,
    navController: NavHostController,
    viewModel: AccountCenterViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentRoute by navController.currentBackStackEntryFlow
        .collectAsState(initial = navController.currentBackStackEntry)

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        stringResource(R.string.panel_de_administrador),
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = 6.dp),
                        thickness = 2.dp
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                        label = { Text("Inicio") },
                        selected = currentRoute?.destination?.route == HOME_SCREEN,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(HOME_SCREEN) {
                                popUpTo(HOME_SCREEN) { inclusive = true }
                            }
                        },
                        shape = MaterialTheme.shapes.small
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 6.dp, bottom = 6.dp),
                        thickness = 2.dp
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        label = { Text("Buscador") },
                        selected = currentRoute?.destination?.route == USER_SEARCH_SCREEN,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(USER_SEARCH_SCREEN)
                        },
                        shape = MaterialTheme.shapes.small
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Filled.Build, contentDescription = null) },
                        label = { Text("Exportar comidas") },
                        selected = currentRoute?.destination?.route == EXPORT_MEALS_SCREEN,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(EXPORT_MEALS_SCREEN)
                        },
                        shape = MaterialTheme.shapes.small
                    )

                    Spacer(Modifier.weight(1f))

                    NavigationDrawerItem(
                        icon = { Icon(painter = painterResource(R.drawable.logout_24px), contentDescription = null) },
                        label = { Text("Cerrar sesión") },
                        selected = false,
                        onClick = { viewModel.onSignOutClick(restartApp) }
                    )

                    Spacer(Modifier.height(12.dp))
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                AppBar(drawerState, scope, navController)
            }
        ) { innerPaddingModifier ->
            Column(modifier = Modifier.padding(innerPaddingModifier)) {
                NavHost(
                    navController = navController,
                    startDestination = HOME_SCREEN
                ) {
                    adminGraph(navController)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.adminGraph(navController: NavHostController) {

    composable(HOME_SCREEN) {
        HomeScreen()
    }

    composable(USER_SEARCH_SCREEN) {
        UserSearchScreen()
    }

    composable(EXPORT_MEALS_SCREEN) {
        ExportMealsScreen()
    }

}
