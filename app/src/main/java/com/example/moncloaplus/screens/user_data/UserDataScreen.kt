package com.example.moncloaplus.screens.user_data

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moncloaplus.R
import com.example.moncloaplus.SnackbarManager
import com.example.moncloaplus.model.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun UserDataScreen (
    openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = hiltViewModel()
) {

    val firstName = viewModel.firstName.collectAsState()
    val firstSurname = viewModel.firstSurname.collectAsState()
    val secondSurname = viewModel.secondSurname.collectAsState()
    val room = viewModel.room.collectAsState()
    val initials = viewModel.initials.collectAsState()
    val city = viewModel.city.collectAsState()
    val degree = viewModel.degree.collectAsState()
    val university = viewModel.university.collectAsState()

    val isFormValid = viewModel.isFormValid.collectAsState(initial = false)
    val isRoomValid = viewModel.isRoomValid.collectAsState().value
    val areInitialsValid = viewModel.areInitialsValid.collectAsState().value

    val snackbarMessage by SnackbarManager.snackbarMessages.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(it)
                SnackbarManager.clearSnackbarState()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val imageRes = if (isSystemInDarkTheme()) R.drawable.cmm_blanco else R.drawable.cmm_negro

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp))

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Moncloa logo",
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp, 4.dp)
                .size(180.dp, 180.dp)
        )

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp))

        Text(
            text = stringResource(R.string.rellena_tus_datos),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp))

        OutlinedTextField(
            singleLine = true,
            label = { Text(stringResource(R.string.nombre)) },
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp, 4.dp),
            shape = RoundedCornerShape(10),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            value = firstName.value,
            onValueChange = { viewModel.updateFirstName(it) },
            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Nombre") }
        )

        OutlinedTextField(
            singleLine = true,
            label = { Text(
                stringResource(R.string.primer_apellido),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            ) },
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp, 4.dp),
            shape = RoundedCornerShape(10),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            value = firstSurname.value,
            onValueChange = { viewModel.updateFirstSurname(it) },
            leadingIcon = { Icon(
                painter = painterResource(R.drawable.looks_one_24px),
                contentDescription = "Primer apellido",
                tint = MaterialTheme.colorScheme.onSurface
            ) }
        )

        OutlinedTextField(
            singleLine = true,
            label = { Text(
                stringResource(R.string.segundo_apellido),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            ) },
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp, 4.dp),
            shape = RoundedCornerShape(10),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            value = secondSurname.value,
            onValueChange = { viewModel.updateSecondSurname(it) },
            leadingIcon = { Icon(
                painter = painterResource(R.drawable.looks_two_24px),
                contentDescription = "Segundo apellido",
                tint = MaterialTheme.colorScheme.onSurface
            ) }
        )

        OutlinedTextField(
            singleLine = true,
            isError = !isRoomValid,
            label = { Text(
                stringResource(R.string.habitacion),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            ) },
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp, 4.dp),
            shape = RoundedCornerShape(10),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            value = room.value,
            placeholder = {Text(stringResource(R.string.formato_habitacion))},
            onValueChange = { viewModel.updateRoom(it) },
            leadingIcon = { Icon(
                painter = painterResource(R.drawable.bed_24px),
                contentDescription = "Habitación",
                tint = MaterialTheme.colorScheme.onSurface
            ) }
        )

        OutlinedTextField(
            singleLine = true,
            isError = !areInitialsValid,
            label = { Text(
                stringResource(R.string.iniciales),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            ) },
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp, 4.dp),
            shape = RoundedCornerShape(10),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            value = initials.value,
            placeholder = {Text(stringResource(R.string.formato_iniciales))},
            onValueChange = { viewModel.updateInitials(it) },
            leadingIcon = { Icon(
                painter = painterResource(R.drawable.laundry_24px),
                contentDescription = "Iniciales",
                tint = MaterialTheme.colorScheme.onSurface
            ) }
        )

        OutlinedTextField(
            singleLine = true,
            label = { Text(stringResource(R.string.ciudad)) },
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp, 4.dp),
            shape = RoundedCornerShape(10),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            value = city.value,
            onValueChange = { viewModel.updateCity(it) },
            leadingIcon = { Icon(
                painter = painterResource(R.drawable.location_city_24px),
                contentDescription = "Ciudad",
                tint = MaterialTheme.colorScheme.onSurface
            ) }
        )

        OutlinedTextField(
            singleLine = true,
            label = { Text(
                stringResource(R.string.carrera),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            ) },
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp, 4.dp),
            shape = RoundedCornerShape(10),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            value = degree.value,
            placeholder = {Text(stringResource(R.string.formato_carrera))},
            onValueChange = { viewModel.updateDegree(it) },
            leadingIcon = { Icon(
                painter = painterResource(R.drawable.school_24px),
                contentDescription = "Carrera",
                tint = MaterialTheme.colorScheme.onSurface
            ) }
        )

        OutlinedTextField(
            singleLine = true,
            label = { Text(
                stringResource(R.string.universidad),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            ) },
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp, 4.dp),
            shape = RoundedCornerShape(10),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            value = university.value,
            placeholder = {Text(stringResource(R.string.formato_universidad))},
            onValueChange = { viewModel.updateUniversity(it) },
            leadingIcon = { Icon(
                painter = painterResource(R.drawable.apartment_24px),
                contentDescription = "Universidad",
                tint = MaterialTheme.colorScheme.onSurface
            ) }
        )

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp))

        Button(
            onClick = { viewModel.saveUserData(openAndPopUp) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp, 0.dp)
                .padding(bottom = 40.dp),
            enabled = isFormValid.value
        ) {
            Text(
                text = stringResource(R.string.guardar_datos),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = modifier.padding(0.dp, 6.dp)
            )
        }

    }

}
