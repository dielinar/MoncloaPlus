package com.example.moncloaplus.screens.export_meals

import com.example.moncloaplus.SnackbarManager
import com.example.moncloaplus.network.RetrofitInstance
import com.example.moncloaplus.screens.PlusViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ExportMealsViewModel @Inject constructor() : PlusViewModel() {

    private val _exportResult = MutableStateFlow("")
    val exportResult: StateFlow<String> = _exportResult.asStateFlow()

    private val _exportUrl = MutableStateFlow("")
    val exportUrl: StateFlow<String> = _exportUrl.asStateFlow()

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private val _selectedWeek = MutableStateFlow("")
    val selectedWeek: StateFlow<String> = _selectedWeek.asStateFlow()

    private val _selectedDay = MutableStateFlow("")
    val selectedDay: StateFlow<String> = _selectedDay.asStateFlow()

    fun updateSelectedWeek(newSelectedWeek: String) { _selectedWeek.value = newSelectedWeek }
    fun updateSelectedDay(newSelectedDay: String) { _selectedDay.value = newSelectedDay }

    fun exportMeals() {
        launchCatching {
            _isExporting.value = true
            try {
                val response = RetrofitInstance.api.exportMeals(_selectedWeek.value, _selectedDay.value)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    _exportResult.value = responseBody?.message ?: "Datos exportados correctamente"
                    _exportUrl.value = responseBody?.exportUrl ?: ""
                    SnackbarManager.showMessage("Datos exportados correctamente.")
                }
                else {
                    _exportResult.value = "Error: ${response.code()}"
                    SnackbarManager.showMessage("Error. Inténtalo de nuevo.")
                }
            }
            catch (e: Exception) {
                _exportResult.value = "Excepción: ${e.localizedMessage}"
                SnackbarManager.showMessage("Error. Inténtalo de nuevo.")
            }
            finally {
                _isExporting.value = false
            }
        }
    }

}
