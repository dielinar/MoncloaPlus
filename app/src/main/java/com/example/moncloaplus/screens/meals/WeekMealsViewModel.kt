package com.example.moncloaplus.screens.meals

import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.moncloaplus.SnackbarManager
import com.example.moncloaplus.data.model.WeekMeals
import com.example.moncloaplus.service.MealsService
import com.example.moncloaplus.screens.PlusViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

    private var lastSelection: MutableMap<String, MutableMap<String, String>> = mutableMapOf()

    private val _templateMeals = MutableStateFlow(mutableMapOf<String, MutableMap<String, String>>())
    val templateMeals: StateFlow<Map<String, Map<String, String>>> = _templateMeals.asStateFlow()

    private val _hasChanges = MutableStateFlow(false)
    val hasChanges: StateFlow<Boolean> = _hasChanges.asStateFlow()

    private val _hasTemplateChanges = MutableStateFlow(false)
    val hasTemplateChanges: StateFlow<Boolean> = _hasTemplateChanges.asStateFlow()

    private val _isTemplateApplied = MutableStateFlow(false)
    val isTemplateApplied: StateFlow<Boolean> = _isTemplateApplied.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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

    fun saveWeek(startDate: String) {
        launchCatching {
            val weekMeals = WeekMeals(id = startDate, meals = _selectedMeals.value)
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
            _isLoading.value = true
            val weekMeals = mealsService.getWeekData(_currentWeekStart.value)
            _selectedMeals.value = weekMeals?.meals
                ?.mapValues { it.value.toMutableMap() }
                ?.toMutableMap() ?: mutableMapOf()
            _isLoading.value = false
        }
    }

    fun nextWeek() {
        _currentWeekStart.update {
            calendar.time = dateFormat.parse(it) ?: Date()
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
            getStartOfWeek(calendar.time)
        }
        getWeekMeals()
        _hasChanges.value = false
    }

    fun previousWeek() {
        _currentWeekStart.update {
            calendar.time = dateFormat.parse(it) ?: Date()
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            getStartOfWeek(calendar.time)
        }
        getWeekMeals()
        _hasChanges.value = false
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
            _isLoading.value = true
            val template = mealsService.getUserTemplate()
            if (template != null) {
                _templateMeals.value = template.meals.mapValues { it.value.toMutableMap() }.toMutableMap()
            }
            _isLoading.value = false
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun clearMeals() {
        val currentDate = LocalDate.now()
        _selectedMeals.update { currentMeals ->
            currentMeals.toMutableMap().apply {
                keys.forEach { day ->
                    val dayDate = getDayDate(day)

                    if (!dayDate.isBefore(currentDate)) {
                        this[day] = this[day]?.keys?.associateWith { "-" }?.toMutableMap() ?: mutableMapOf()
                    }
                }
            }
        }
        saveWeek(_currentWeekStart.value)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toggleTemplate(isChecked: Boolean) {
        val currentDate = LocalDate.now()
        _isTemplateApplied.value = isChecked

        if (isChecked) {
            lastSelection = _selectedMeals.value.toMutableMap()
            _selectedMeals.update { currentMeals ->
                currentMeals.toMutableMap().apply {
                    _templateMeals.value.forEach { (day, meals) ->
                        val dayDate = getDayDate(day)

                        if (!dayDate.isBefore(currentDate)) {
                            this[day] = meals.toMutableMap()
                        }
                    }
                }
            }
            _hasChanges.value = true
        } else {
            _selectedMeals.value = lastSelection
            _hasChanges.value = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDayDate(day: String): LocalDate {
        val weekStart = LocalDate.parse(_currentWeekStart.value, DateTimeFormatter.ofPattern(
            DATE_PATTERN
        ))
        val dayIndex = WEEK_DAYS.indexOf(day)
        return weekStart.plusDays(dayIndex.toLong())
    }

    fun clearTemplate() {
        _templateMeals.update { currentTemplate ->
            currentTemplate.toMutableMap().apply {
                keys.forEach { day ->
                    this[day] = this[day]?.keys?.associateWith { "-" }?.toMutableMap() ?: mutableMapOf()
                }
            }
        }
        saveTemplate()
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDay(): String {
        return LocalDate.now()
            .format(DateTimeFormatter.ofPattern("EEEE", Locale("es", "ES")))
            .replaceFirstChar { it.uppercase() }
    }

}
