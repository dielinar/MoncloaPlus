package com.example.moncloaplus.model

import android.icu.util.Calendar
import com.example.moncloaplus.SnackbarManager
import com.example.moncloaplus.model.service.MealsService
import com.example.moncloaplus.screens.PlusViewModel
import com.example.moncloaplus.screens.meals.DATE_PATTERN
import com.example.moncloaplus.screens.meals.WEEK_DAYS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WeekMealsViewModel @Inject constructor(
    private val mealsService: MealsService
): PlusViewModel() {

    private val dateFormat = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
    private val calendar = Calendar.getInstance()

    private val _currentWeekStart = MutableStateFlow(getStartOfWeek(calendar.time))
    val currentWeekStart: StateFlow<String> = _currentWeekStart.asStateFlow()

    private val _selectedMeals = MutableStateFlow(mutableMapOf<String, MutableMap<String, String>>())
    val selectedMeals: StateFlow<Map<String, Map<String, String>>> = _selectedMeals.asStateFlow()

    private val _templateMeals = MutableStateFlow(mutableMapOf<String, MutableMap<String, String>>())
    val templateMeals: StateFlow<Map<String, Map<String, String>>> = _templateMeals.asStateFlow()

    private val _hasChanges = MutableStateFlow(false)
    val hasChanges: StateFlow<Boolean> = _hasChanges.asStateFlow()

    private val _hasTemplateChanges = MutableStateFlow(false)
    val hasTemplateChanges: StateFlow<Boolean> = _hasTemplateChanges.asStateFlow()

    private val _isTemplateApplied = MutableStateFlow(false)
    val isTemplateApplied: StateFlow<Boolean> = _isTemplateApplied.asStateFlow()

    init {
        getWeekMeals()
        getTemplate()
    }

    fun updateMeal(day: String, mealType: String, value: String) {
        _selectedMeals.update { currentMeals ->
            currentMeals.toMutableMap().apply {
                this[day] = this[day]?.toMutableMap()?.apply {
                    this[mealType] = value
                } ?: mutableMapOf(mealType to value)
            }
        }
        _hasChanges.value = true
    }

    fun clearMeals() {
        _selectedMeals.update { currentMeals ->
            currentMeals.toMutableMap().apply {
                keys.forEach { day ->
                    this[day] = this[day]?.keys?.associateWith { "-" }?.toMutableMap() ?: mutableMapOf()
                }
            }
        }
        _hasChanges.value = true
        _isTemplateApplied.value = false
    }

    fun saveWeek(startDate: String) {
        launchCatching {
            val weekMeals = WeekMeals(id = startDate, meals = selectedMeals.value)
            mealsService.saveWeekData(weekMeals)
            _isTemplateApplied.value = false
            _hasChanges.value = false
            SnackbarManager.showMessage("Guardado correctamente.")
        }
    }

    fun getWeekDays(startDate: String): List<Pair<String, String>> {
        val startCalendar = Calendar.getInstance().apply {
            time = dateFormat.parse(startDate) ?: Date()
        }
        return WEEK_DAYS.mapIndexed { index, day ->
            val date = Calendar.getInstance().apply {
                time = startCalendar.time
                add(Calendar.DAY_OF_MONTH, index)
            }
            day to dateFormat.format(date.time)
        }
    }

    private fun getWeekMeals() {
        launchCatching {
            val weekMeals = mealsService.getWeekData(_currentWeekStart.value)
            _selectedMeals.value = weekMeals?.meals
                ?.mapValues { it.value.toMutableMap() }
                ?.toMutableMap() ?: mutableMapOf()
        }
    }

    fun nextWeek() {
        _currentWeekStart.update {
            calendar.time = dateFormat.parse(it) ?: Date()
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
            getStartOfWeek(calendar.time)
        }
        getWeekMeals()
    }

    fun previousWeek() {
        _currentWeekStart.update {
            calendar.time = dateFormat.parse(it) ?: Date()
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            getStartOfWeek(calendar.time)
        }
        getWeekMeals()
    }

    fun setCurrentWeek() {
        _currentWeekStart.value = getStartOfWeek(Date())
        getWeekMeals()
    }

    private fun getStartOfWeek(date: Date): String {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        return dateFormat.format(calendar.time)
    }

    fun saveTemplate() {
        launchCatching {
            val template = WeekMeals(id = "", meals = _templateMeals.value)
            mealsService.saveTemplateData(template)
            _hasTemplateChanges.value = false
            SnackbarManager.showMessage("Plantilla guardada correctamente.")
        }
    }

    private fun getTemplate() {
        launchCatching {
            val template = mealsService.getUserTemplate()
            if (template != null) {
                _templateMeals.value = template.meals.mapValues { it.value.toMutableMap() }.toMutableMap()
            }
        }
    }

    fun updateTemplate(day: String, mealType: String, value: String) {
        _templateMeals.update { currentMeals ->
            currentMeals.toMutableMap().apply {
                this[day] = this[day]?.toMutableMap()?.apply {
                    this[mealType] = value
                } ?: mutableMapOf(mealType to value)
            }
        }
        _hasTemplateChanges.value = true
    }

    fun applyTemplate() {
        _selectedMeals.value = _templateMeals.value
        _isTemplateApplied.value = true
        _hasChanges.value = true
    }

    fun clearTemplate() {
        _templateMeals.update { currentTemplate ->
            currentTemplate.toMutableMap().apply {
                keys.forEach { day ->
                    this[day] = this[day]?.keys?.associateWith { "-" }?.toMutableMap() ?: mutableMapOf()
                }
            }
        }
        _hasTemplateChanges.value = true
    }

    fun getUpcomingWeeks(): List<String> {
        val weeks = mutableListOf<String>()
        val tempCalendar = Calendar.getInstance()

        repeat(3) {
            weeks.add(getStartOfWeek(tempCalendar.time))
            tempCalendar.add(Calendar.WEEK_OF_YEAR, 1)
        }

        return weeks
    }

}