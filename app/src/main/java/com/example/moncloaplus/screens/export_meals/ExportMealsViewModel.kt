package com.example.moncloaplus.screens.export_meals

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

    fun exportMeals(date: String, day: String) {
        launchCatching {
            try {
                val response = RetrofitInstance.api.exportMeals(date, day)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    _exportResult.value = responseBody?.message ?: "Datos exportados correctamente"
                    _exportUrl.value = responseBody?.exportUrl ?: ""
                }
                else {
                    _exportResult.value = "Error: ${response.code()}"
                }
            }
            catch (e: Exception) {
                _exportResult.value = "Excepción: ${e.localizedMessage}"
            }
        }
    }

}