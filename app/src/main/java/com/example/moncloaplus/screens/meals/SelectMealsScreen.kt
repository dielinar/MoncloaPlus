package com.example.moncloaplus.screens.meals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moncloaplus.R
import com.example.moncloaplus.SnackbarManager
import com.example.moncloaplus.model.WeekMealsViewModel
import com.example.moncloaplus.utils.DATE_PATTERN
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SelectMealsScreen(
    modifier: Modifier = Modifier,
    viewModel: WeekMealsViewModel = hiltViewModel()
) {

    val selectedMeals by viewModel.selectedMeals.collectAsState()
    val currentWeekStart by viewModel.currentWeekStart.collectAsState()
    val hasChanges by viewModel.hasChanges.collectAsState()
    val isTemplateApplied by viewModel.isTemplateApplied.collectAsState()

    val currentDate = LocalDate.now()
    val selectedWeekStartDate = LocalDate.parse(currentWeekStart, DateTimeFormatter.ofPattern(
        DATE_PATTERN
    ))
    val isPastWeek = selectedWeekStartDate.isBefore(currentDate.with(DayOfWeek.MONDAY))

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
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SaveMealsButton (
                buttonText = stringResource(R.string.guardar_comidas),
                onSave = { viewModel.saveWeek(currentWeekStart) },
                enabled = hasChanges
            )
            Spacer(Modifier.weight(1f))
            ClearMealsButton (
                onClear = { viewModel.clearMeals() },
                title = stringResource(R.string.borrar_comidas),
                dialog = stringResource(R.string.confirmar_borrar_comidas),
                enabled = !isPastWeek
            )
            ApplyMealsTemplateFilterChip(
                isSelected = isTemplateApplied,
                onCheckedChange = { isChecked ->
                    viewModel.toggleTemplate(isChecked)
                },
                enabled = !isPastWeek
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SingleChoiceSegmentedButton(
                NAVIGATION_DATE_BAR_OPTIONS,
                onOptionClick = {
                    when (it) {
                        NAVIGATION_DATE_BAR_OPTIONS[0] -> viewModel.previousWeek()
                        NAVIGATION_DATE_BAR_OPTIONS[1] -> viewModel.setCurrentWeek()
                        NAVIGATION_DATE_BAR_OPTIONS[2] -> viewModel.nextWeek()
                    }
                },
                isTemplateApplied = isTemplateApplied
            )
        }

        viewModel.getWeekDays(currentWeekStart).forEach { (day, date) ->
            MealScheduleRow(day, date, selectedMeals[day] ?: emptyMap()) { mealType, value ->
                viewModel.updateMeal(day, mealType, value)
            }
        }
    }
}











