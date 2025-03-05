package com.example.moncloaplus.screens.authentication.sign_in

import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.example.moncloaplus.HOME_SCREEN
import com.example.moncloaplus.SIGN_IN_SCREEN
import com.example.moncloaplus.SnackbarManager
import com.example.moncloaplus.USER_DATA_SCREEN
import com.example.moncloaplus.model.service.AccountService
import com.example.moncloaplus.model.service.StorageService
import com.example.moncloaplus.screens.PlusViewModel
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService
) : PlusViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun updateEmail(newEmail: String) { _email.value = newEmail }
    fun updatePassword(newPassword: String) { _password.value = newPassword }

    fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            try {
                accountService.signInWithEmail(_email.value, _password.value)
                openAndPopUp(HOME_SCREEN, SIGN_IN_SCREEN)
            }
            catch (e: FirebaseAuthInvalidUserException) {
                SnackbarManager.showMessage("Esta cuenta no existe. Regístrate primero.")
            }
            catch (e: FirebaseAuthInvalidCredentialsException) {
                SnackbarManager.showMessage("Correo o contraseña incorrectos.")
            }
            catch (e: Exception) {
                SnackbarManager.showMessage("Error al iniciar sesión. Inténtalo de nuevo.")
            }
        }
    }

    fun onSignInWithGoogle(credential: Credential, openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            try {
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val userId = accountService.signInWithGoogle(googleIdTokenCredential.idToken)

                    val user = storageService.getUser(userId)

                    if (user != null) {
                        openAndPopUp(HOME_SCREEN, SIGN_IN_SCREEN)
                    } else {
                        openAndPopUp(USER_DATA_SCREEN, SIGN_IN_SCREEN)
                    }
                } else {
                    SnackbarManager.showMessage("Error inesperado en la autenticación con Google.")
                }
            } catch (e: FirebaseAuthInvalidUserException) {
                SnackbarManager.showMessage("La cuenta de Google no está registrada.")
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                SnackbarManager.showMessage("Error en las credenciales de Google. Inténtalo de nuevo.")
            } catch (e: Exception) {
                SnackbarManager.showMessage("Error al iniciar sesión con Google: ${e.message}")
            }
        }
    }

}
