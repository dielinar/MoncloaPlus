package com.example.moncloaplus.screens.meals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moncloaplus.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleChoiceSegmentedButton(options: List<String>, onOptionClick: (String) -> Unit) {
    var selectedOption by remember { mutableStateOf(options[1]) }

    SingleChoiceSegmentedButtonRow (
        modifier = Modifier.graphicsLayer(scaleX = 0.8f, scaleY = 0.8f)
    ){
        options.forEachIndexed { index, label ->
            val isHoy = label == options[1]
            val isSelected = selectedOption == label
            val borderWidth = if (isSelected && !isHoy) 2.dp else SegmentedButtonDefaults.BorderWidth
            val borderColor = if (isSelected && !isHoy) MaterialTheme.colorScheme.primary else SegmentedButtonDefaults.colors().inactiveBorderColor

            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = {
                    selectedOption = label
                    onOptionClick(label)
                },
                selected = isSelected,
                label = { Text(label) },
                icon = {},
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = if (isHoy) MaterialTheme.colorScheme.primary.copy(0.2f) else Color.Transparent,
                    activeContentColor = if (isHoy) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary,
                ),
                border = BorderStroke(borderWidth, borderColor)
            )
        }
    }
}

@Composable
fun ClearMealsButton(onClear: () -> Unit, title: String, dialog: String) {
    var showDialog by remember { mutableStateOf(false) }

    SmallFloatingActionButton(
        onClick = { showDialog = true },
        modifier = Modifier
            .size(40.dp)
            .graphicsLayer(0.9f, 0.9f),
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.tertiary
    ) {
        Icon(
            painter = painterResource(R.drawable.delete_24px),
            contentDescription = null
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = { Icon(Icons.Default.Warning, null) },
            title = { Text(text = title) },
            text = { Text(text = dialog) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClear()
                        showDialog = false
                    }
                ) { Text(stringResource(R.string.confirmar)) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancelar))
                }
            }
        )
    }
}

@Composable
fun ApplyMealsTemplateButton(isEnabled: Boolean, onApplyTemplate: () -> Unit) {
    OutlinedButton(
        onClick = onApplyTemplate,
        enabled = !isEnabled,
        shape = RoundedCornerShape(20),
        modifier = Modifier
            .height(40.dp)
            .graphicsLayer(0.9f, 0.9f),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = ButtonDefaults.outlinedButtonColors().disabledContainerColor,
            contentColor = MaterialTheme.colorScheme.tertiary,
            disabledContentColor = MaterialTheme.colorScheme.onTertiary,
            disabledContainerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Text(text = stringResource(R.string.aplicar_plantilla))
    }
}

@Composable
fun SaveMealsButton(buttonText: String, onSave: () -> Unit, enabled: Boolean) {
    OutlinedButton (
        onClick = { if (enabled) onSave() },
        modifier = Modifier
            .padding(start = 10.dp, top = 4.dp)
            .graphicsLayer(scaleX = 1.15f, scaleY = 1.15f),
        shape = RoundedCornerShape(20),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
    ) {
        Text(text = buttonText)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDropdown(label: String, options: List<String>, selected: String?, onSelectionChange: (String) -> Unit) {

    var expanded by remember { mutableStateOf(false) }
    val selectedOption = selected ?: "-"

    val dark = isSystemInDarkTheme()
    val containerColor = when (selectedOption) {
        "-" ->
            if (label == "Desayuno") MealColors.breakfastContainer()
            else if (label == "Comida") MealColors.lunchContainer()
            else MealColors.dinnerContainer()
        "Pronto" -> MealColors.prontoContainer
        "Normal" -> MealColors.normalContainer()
        "Tarde" -> if (dark) MealColors.tardeContainerDark else MealColors.tardeContainerLight
        "X" -> MealColors.noSelectionContainer
        else -> Color.Unspecified
    }
    val onContainerColor = when (selectedOption) {
        "Pronto", "Tarde" -> Color.Black
        else -> Color.Unspecified
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontSize = 9.sp) },
            textStyle = TextStyle(
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor,
                focusedTextColor = onContainerColor,
                unfocusedTextColor = onContainerColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelectionChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MealScheduleRow(day: String, date: String?, selectedMeals: Map<String, String>, onMealSelected: (String, String) -> Unit) {

    val today = remember { java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern(DATE_PATTERN)) }
    val isToday = date == today
    val backgroundColor = if (isToday) MaterialTheme.colorScheme.primary.copy(0.2f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .background(backgroundColor, RoundedCornerShape(10))
            .padding(vertical = 8.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.width(80.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day,
                style = TextStyle(fontSize = TextUnit.Unspecified),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.padding(4.dp)
            )
            if (date != null) {
                Text(
                    text = date,
                    style = TextStyle(fontSize = TextUnit.Unspecified),
                    maxLines = 1
                )
            }
        }
        Spacer(modifier = Modifier
            .fillMaxHeight()
            .padding(2.dp))
        Box(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) { MealDropdown("Desayuno", BREAKFAST_OPTIONS, selectedMeals["Desayuno"]) { onMealSelected("Desayuno", it) } }
                Box(modifier = Modifier.weight(1f)) { MealDropdown("Comida", LUNCH_OPTIONS, selectedMeals["Comida"]) { onMealSelected("Comida", it) } }
                Box(modifier = Modifier.weight(1f)) { MealDropdown("Cena", DINNER_OPTIONS, selectedMeals["Cena"]) { onMealSelected("Cena", it) } }
            }
        }
    }
}