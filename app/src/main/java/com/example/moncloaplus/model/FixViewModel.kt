package com.example.moncloaplus.model

import android.net.Uri
import com.example.moncloaplus.SnackbarManager
import com.example.moncloaplus.model.service.AccountService
import com.example.moncloaplus.model.service.FixService
import com.example.moncloaplus.model.service.StorageService
import com.example.moncloaplus.model.service.impl.AccountServiceImpl
import com.example.moncloaplus.screens.PlusViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FixViewModel @Inject constructor(
    private val fixService: FixService,
    private val accountService: AccountService,
    private val storageService: StorageService
): PlusViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _userFixes = MutableStateFlow<Map<Int, List<Fix>>>(emptyMap())
    val userFixes: StateFlow<Map<Int, List<Fix>>> = _userFixes.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri.asStateFlow()

    fun updateDescription(newDescription: String) { _description.value = newDescription }
    fun updateImageUri(newUri: Uri) { _imageUri.value = newUri }

    fun addFix() {
        launchCatching {
            _isLoading.value = true

            val currentUser = storageService.getUser(accountService.currentUserId)

            val fix = Fix(
                id = "",
                fecha = Timestamp.now(),
                localizacion = currentUser!!.roomNumber,
                descripcion = _description.value,
                estado = FixState.PENDING,
                imagen = Fix.FixImage(),
                owner = currentUser
            )
            val newFix = fixService.addFix(fix, imageUri.value)

            SnackbarManager.showMessage("Arreglo creado correctamente.")

            addToUserFixes(newFix)

            resetValues()

            _isLoading.value = false
        }
    }

    fun resetValues() {
        _description.value = ""
        _imageUri.value = null
    }

    private fun addToUserFixes(fix: Fix) {
        val stateKey = fix.estado.ordinal
        _userFixes.value = _userFixes.value.toMutableMap().apply {
            val currentList = this[stateKey] ?: emptyList()
            put(stateKey, (currentList + fix).sortedBy { it.fecha })
        }
    }

}
