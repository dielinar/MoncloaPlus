package com.example.moncloaplus.screens.meals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moncloaplus.R
import com.example.moncloaplus.SnackbarManager
import com.example.moncloaplus.model.WeekMealsViewModel
import com.example.moncloaplus.screens.reservation.LoadingIndicator
import com.example.moncloaplus.utils.WEEK_DAYS
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MealsTemplateScreen(
    modifier: Modifier = Modifier,
    viewModel: WeekMealsViewModel = hiltViewModel()
) {
    val templateMeals by viewModel.templateMeals.collectAsState()
    val hasTemplateChanges by viewModel.hasTemplateChanges.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarMessage by SnackbarManager.snackbarMessages.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.getTemplate()
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(it)
                SnackbarManager.clearSnackbarState()
            }
        }
    }

    if (isLoading) {
        LoadingIndicator()
    } else {
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
                    buttonText = stringResource(R.string.guardar_plantilla),
                    onSave = { viewModel.saveTemplate() },
                    enabled = hasTemplateChanges,
                )
                Spacer(Modifier.weight(1f))
                ClearMealsButton (
                    onClear = { viewModel.clearTemplate() },
                    title = stringResource(R.string.borrar_plantilla),
                    dialog = stringResource(R.string.descripcion_borrar_plantilla),
                    enabled = true
                )
            }

            WEEK_DAYS.forEach { day ->
                MealScheduleRow(day, null, templateMeals[day] ?: emptyMap()) { mealType, value ->
                    viewModel.updateTemplate(day, mealType, value)
                }
            }
        }
    }

}