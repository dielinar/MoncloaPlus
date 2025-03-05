package com.example.moncloaplus.screens.export_meals

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moncloaplus.R
import com.example.moncloaplus.model.WeekMealsViewModel
import com.example.moncloaplus.screens.meals.WEEK_DAYS
import com.example.moncloaplus.screens.meals.getExactDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExportMealsScreen(
    mealsViewModel: WeekMealsViewModel = hiltViewModel(),
    exportViewModel: ExportMealsViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val exportResult by exportViewModel.exportResult.collectAsState()
    val exportUrl by exportViewModel.exportUrl.collectAsState()
    val isExporting by exportViewModel.isExporting.collectAsState()

    val weeks = mealsViewModel.getUpcomingWeeks()

    val selectedWeek by exportViewModel.selectedWeek.collectAsState()
    val selectedDay by exportViewModel.selectedDay.collectAsState()

    var shouldDownload by remember { mutableStateOf(false) }

    val initialWeek = weeks.firstOrNull() ?: ""
    val initialDay = WEEK_DAYS.firstOrNull() ?: ""

    if (selectedWeek.isEmpty()) exportViewModel.updateSelectedWeek(initialWeek)
    if (selectedDay.isEmpty()) exportViewModel.updateSelectedDay(initialDay)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        DropdownSelector(
            label = stringResource(R.string.selecciona_la_semana),
            options = weeks,
            selected = selectedWeek,
            onSelectionChange = { exportViewModel.updateSelectedWeek(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        DropdownSelector(
            label = stringResource(R.string.selecciona_el_dia),
            options = WEEK_DAYS,
            selected = selectedDay,
            onSelectionChange = { exportViewModel.updateSelectedDay(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                shouldDownload = true
                exportViewModel.exportMeals()
            }
        ) {
            Text(stringResource(R.string.exportar_y_descargar))
        }

        if (isExporting) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        if (exportResult.isNotEmpty()) {
            if (shouldDownload && exportResult.contains("Datos exportados", ignoreCase = true) && exportUrl.isNotEmpty()) {
                val exactDate = getExactDate(selectedWeek, selectedDay)
                val fileName = "${selectedDay}_${exactDate}.pdf"
                startDownload(context, exportUrl, fileName)
                shouldDownload = false
            }
        }
    }
}

@Composable
fun DropdownSelector(label: String, options: List<String>, selected: String?, onSelectionChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selectedOption = selected ?: ""

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)

        Box {
            OutlinedButton(
                onClick = { expanded = true },
            ) {
                Text(text = selectedOption)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
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
}

fun startDownload(context: Context, url: String, fileName: String) {
    val request = DownloadManager.Request(Uri.parse(url))
        .setTitle("Descargando $fileName")
        .setDescription("Descargando archivo de Google Sheets...")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setAllowedOverMetered(true)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        request.setDestinationInExternalFilesDir(
            context,
            Environment.DIRECTORY_DOWNLOADS,
            fileName
        )
    } else {
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
           fileName
        )
    }

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
}
