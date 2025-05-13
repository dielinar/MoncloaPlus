package com.example.moncloaplus.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.moncloaplus.HOME_SCREEN
import com.example.moncloaplus.R
import com.example.moncloaplus.data.model.User
import com.example.moncloaplus.screens.authentication.AccountCenterViewModel
import com.example.moncloaplus.screens.admin.export_meals.ExportMealsScreen
import com.example.moncloaplus.screens.create_event.CreateEventScreen
import com.example.moncloaplus.screens.fixes.FixesScreen
import com.example.moncloaplus.screens.home.CREATE_EVENT_SCREEN
import com.example.moncloaplus.screens.home.EXPORT_MEALS_SCREEN
import com.example.moncloaplus.screens.home.FIXES_SCREEN
import com.example.moncloaplus.screens.home.HomeScreen
import com.example.moncloaplus.screens.home.MEALS_TEMPLATE_SCREEN
import com.example.moncloaplus.screens.home.RESERVATION_SCREEN
import com.example.moncloaplus.screens.home.SELECT_MEALS_SCREEN
import com.example.moncloaplus.screens.home.USER_SEARCH_SCREEN
import com.example.moncloaplus.screens.meals.MealsTemplateScreen
import com.example.moncloaplus.screens.meals.SelectMealsScreen
import com.example.moncloaplus.screens.reservation.ReservationScreen
import com.example.moncloaplus.screens.user_search.UserSearchScreen
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    restartApp: (String) -> Unit,
    navController: NavHostController,
    viewModel: AccountCenterViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val user by viewModel.user.collectAsState(initial = User())

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
                        "¡Hola, ${user.firstName}!",
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

                    if (user.canAddEvents()) {
                        NavigationDrawerItem(
                            icon = { Icon(painter = painterResource(R.drawable.edit_calendar_24px), contentDescription = null) },
                            label = { Text("Añadir evento") },
                            selected = currentRoute?.destination?.route == CREATE_EVENT_SCREEN,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate(CREATE_EVENT_SCREEN)
                            },
                            shape = MaterialTheme.shapes.small
                        )
                    }

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
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 6.dp, bottom = 6.dp),
                        thickness = 2.dp
                    )

                    NavigationDrawerItem(
                        icon = { Icon(painter = painterResource(R.drawable.restaurant_24px), contentDescription = null) },
                        label = { Text("Comidas") },
                        selected = currentRoute?.destination?.route == SELECT_MEALS_SCREEN,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(SELECT_MEALS_SCREEN)
                        },
                        shape = MaterialTheme.shapes.small
                    )
                    NavigationDrawerItem(
                        icon = { Icon(painter = painterResource(R.drawable.receipt_long_24px), contentDescription = null) },
                        label = { Text("Plantilla comidas") },
                        selected = currentRoute?.destination?.route == MEALS_TEMPLATE_SCREEN,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(MEALS_TEMPLATE_SCREEN)
                        },
                        shape = MaterialTheme.shapes.small
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 6.dp, bottom = 6.dp),
                        thickness = 2.dp
                    )

                    NavigationDrawerItem(
                        icon = { Icon(painter = painterResource(R.drawable.bookmark_added_24px), contentDescription = null) },
                        label = { Text("Reservas") },
                        selected = currentRoute?.destination?.route == RESERVATION_SCREEN,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(RESERVATION_SCREEN)
                        },
                        shape = MaterialTheme.shapes.small
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 6.dp, bottom = 6.dp),
                        thickness = 2.dp
                    )

                    NavigationDrawerItem(
                        icon = { Icon(painter = painterResource(R.drawable.handyman_24px__1_), contentDescription = null) },
                        label = { Text("Arreglos") },
                        selected = currentRoute?.destination?.route == FIXES_SCREEN,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(FIXES_SCREEN)
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
                    mainGraph(navController)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mainGraph(navController: NavHostController) {

    composable(HOME_SCREEN) {
        HomeScreen()
    }

    composable(USER_SEARCH_SCREEN) {
        UserSearchScreen()
    }

    composable(SELECT_MEALS_SCREEN) {
        SelectMealsScreen()
    }

    composable(MEALS_TEMPLATE_SCREEN) {
        MealsTemplateScreen()
    }

    composable(RESERVATION_SCREEN) {
        val navigationController = rememberNavController()
        ReservationScreen(
            navController = navigationController
        )
    }

    composable(EXPORT_MEALS_SCREEN) {
        ExportMealsScreen()
    }

    composable(FIXES_SCREEN) {
        FixesScreen()
    }

    composable(CREATE_EVENT_SCREEN) {
        CreateEventScreen(navController = navController)
    }

}
