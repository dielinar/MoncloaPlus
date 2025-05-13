package com.example.moncloaplus.screens.authentication.sign_up

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moncloaplus.R
import com.example.moncloaplus.SnackbarManager
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel()
) {

    val email = viewModel.email.collectAsState()
    val password = viewModel.password.collectAsState()
    val confirmPassword = viewModel.confirmPassword.collectAsState()
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
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Moncloa logo",
            modifier = modifier.size(180.dp, 180.dp)
        )

        Spacer(modifier = Modifier.padding(8.dp))

        OutlinedTextField(
            singleLine = true,
            label = {Text(stringResource(R.string.correo_electronico))},
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp, 4.dp),
            shape = RoundedCornerShape(10),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            value = email.value,
            onValueChange = { viewModel.updateEmail(it) },
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email") }
        )

        OutlinedTextField(
            singleLine = true,
            label = {Text(stringResource(R.string.contraseña))},
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp, 4.dp),
            shape = RoundedCornerShape(10),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary
            ),
            value = password.value,
            onValueChange = { viewModel.updatePassword(it) },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            singleLine = true,
            label = {Text(stringResource(R.string.confirmar_contraseña))},
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp, 4.dp),
            shape = RoundedCornerShape(10),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary
            ),
            value = confirmPassword.value,
            onValueChange = { viewModel.updateConfirmPassword(it) },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Confirm Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp))

        Button(
            onClick = { viewModel.onSignUpClick(openAndPopUp) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp, 0.dp)
        ) {
            Text(
                text = stringResource(R.string.registrarse),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = modifier.padding(0.dp, 6.dp)
            )
        }

    }
}
