package com.example.moncloaplus.screens.export_meals

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ExportMealsScreen(
    viewModel: ExportMealsViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val exportResult by viewModel.exportResult.collectAsState()
    val exportUrl by viewModel.exportUrl.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                viewModel.exportMeals("03-03-2025", "Sábado")
            }
        ) {
            Text("Exportar y descargar Google Sheets")
        }

        if (exportResult.isNotEmpty()) {
            Text(text = exportResult)
            if (exportResult.contains("Datos exportados", ignoreCase = true) && exportUrl.isNotEmpty()) {
                startDownload(context, exportUrl)
            }
        }
    }

}

fun startDownload(context: Context, url: String) {
    val request = DownloadManager.Request(Uri.parse(url))
        .setTitle("Descarga de Google Sheets")
        .setDescription("Descargando archivo de Google Sheets...")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setAllowedOverMetered(true)

    // Verifica la versión de Android
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Android 10+ usa getExternalFilesDir()
        request.setDestinationInExternalFilesDir(
            context,
            Environment.DIRECTORY_DOWNLOADS,
            "GoogleSheets.pdf"
        )
    } else {
        // Android 9 y versiones anteriores usan setDestinationInExternalPublicDir()
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "GoogleSheets.pdf"
        )
    }

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
}
