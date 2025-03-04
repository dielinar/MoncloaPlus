package com.example.moncloaplus.screens.authentication.sign_up

import com.example.moncloaplus.SIGN_UP_SCREEN
import com.example.moncloaplus.SnackbarManager
import com.example.moncloaplus.USER_DATA_SCREEN
import com.example.moncloaplus.model.service.AccountService
import com.example.moncloaplus.screens.PlusViewModel
import com.example.moncloaplus.screens.authentication.isValidEmail
import com.example.moncloaplus.screens.authentication.isValidPassword
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val accountService: AccountService
) : PlusViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            if (!_email.value.isValidEmail()) {
                SnackbarManager.showMessage("Plantilla guardada correctamente")
                return@launchCatching
            }

            if (_password.value != _confirmPassword.value) {
                SnackbarManager.showMessage("Las contraseñas no coinciden.")
                return@launchCatching
            }

            if (!_password.value.isValidPassword()) {
                SnackbarManager.showMessage("La contraseña debe tener al menos 6 dígitos e incluir un dígito, una letra minúscula y una letra mayúscula.")
                return@launchCatching
            }

            try {
                accountService.signUpWithEmail(_email.value, _password.value)
                openAndPopUp(USER_DATA_SCREEN, SIGN_UP_SCREEN)
            } catch (e: FirebaseAuthUserCollisionException) {
                SnackbarManager.showMessage("Este correo ya está registrado. Intenta iniciar sesión.")
            } catch (e: FirebaseAuthWeakPasswordException) {
                SnackbarManager.showMessage("La contraseña debe tener al menos 6 dígitos e incluir un dígito, una letra minúscula y una letra mayúscula.")
            } catch (e: Exception) {
                SnackbarManager.showMessage("Error desconocido al registrarse.")
            }
        }
    }
}
