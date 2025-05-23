package com.example.moncloaplus.screens.fixes

import android.net.Uri
import com.example.moncloaplus.SnackbarManager
import com.example.moncloaplus.data.model.Fix
import com.example.moncloaplus.data.model.FixState
import com.example.moncloaplus.service.AccountService
import com.example.moncloaplus.service.FixService
import com.example.moncloaplus.service.StorageService
import com.example.moncloaplus.screens.PlusViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FixViewModel @Inject constructor(
    private val fixService: FixService,
    private val accountService: AccountService,
    private val storageService: StorageService
): PlusViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri.asStateFlow()

    private val _userFixes = MutableStateFlow<Map<Int, List<Fix>>>(emptyMap())
    val userFixes: StateFlow<Map<Int, List<Fix>>> = _userFixes.asStateFlow()

    private val _allFixes = MutableStateFlow<Map<Int, List<Fix>>>(emptyMap())
    val allFixes: StateFlow<Map<Int, List<Fix>>> = _allFixes.asStateFlow()

    fun updateDescription(newDescription: String) { _description.value = newDescription }
    fun updateImageUri(newUri: Uri) { _imageUri.value = newUri }

    fun createFix() {
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
            val newFix = fixService.createFix(fix, imageUri.value)

            addToUserFixes(newFix)
            _isLoading.value = false
            resetValues()
            SnackbarManager.showMessage("Arreglo creado correctamente.")
        }
    }

    fun deleteFix(fix: Fix) {
        launchCatching {
            _isLoading.value = true
            fixService.deleteFix(fix)

            removeFromUserFixes(fix.id, fix.estado.ordinal)

            _isLoading.value = false
            SnackbarManager.showMessage("Arreglo eliminado correctamente.")
        }
    }

    private fun resetValues() {
        updateDescription("")
        _imageUri.value = null
    }

    fun fetchUserFixes(state: Int) {
        launchCatching {
            _isLoading.value = true

            val fixesList = fixService.getUserFixes(state)
            _userFixes.value = _userFixes.value.toMutableMap().apply {
                put(state, fixesList)
            }

            _isLoading.value = false
        }
    }

    fun fetchAllFixes(state: Int) {
        launchCatching {
            _isLoading.value = true
            val fixesList = fixService.getAllFixesByState(state)
            _allFixes.value = _allFixes.value.toMutableMap().apply {
                put(state, fixesList)
            }
            _isLoading.value = false
        }
    }

    fun updateFixState(fix: Fix, newState: FixState) {
        launchCatching {
            _isLoading.value = true
            val oldState = fix.estado.ordinal

            val updatedFix = fix.copy(estado = newState)
            fixService.updateFixState(updatedFix, newState)

            moveFixBetweenStates(updatedFix, oldState, newState.ordinal)

            _isLoading.value = false
            SnackbarManager.showMessage("Estado actualizado correctamente.")
        }
    }

    private fun moveFixBetweenStates(fix: Fix, oldState: Int, newState: Int) {
        _allFixes.value = _allFixes.value.toMutableMap().apply {
            val updatedOldList = get(oldState)?.filterNot { it.id == fix.id } ?: emptyList()
            put(oldState, updatedOldList)

            val cleanedNewList = get(newState)?.filterNot { it.id == fix.id }?.toMutableList() ?: mutableListOf()
            cleanedNewList.add(fix)
            cleanedNewList.sortByDescending { it.fecha }

            put(newState, cleanedNewList)
        }
    }

    private fun addToUserFixes(fix: Fix) {
        val stateKey = fix.estado.ordinal
        _userFixes.value = _userFixes.value.toMutableMap().apply {
            val updatedFixes = get(stateKey)?.toMutableList() ?: mutableListOf()
            updatedFixes.add(fix)
            updatedFixes.sortByDescending { it.fecha }
            put(stateKey, updatedFixes)
        }
    }

    private fun removeFromUserFixes(fixId: String, state: Int) {
        _userFixes.value = _userFixes.value.toMutableMap().apply {
            val updatedFixes = get(state)?.filterNot { it.id == fixId } ?: emptyList()
            put(state, updatedFixes)
        }
    }

}
