package com.example.moncloaplus.screens.fixes

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.moncloaplus.model.Fix
import com.example.moncloaplus.model.FixViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.moncloaplus.R
import com.example.moncloaplus.model.FixState
import java.text.SimpleDateFormat

@Composable
fun FixesList(
    fixesList: List<Fix>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(fixesList) { fix ->
            FixCard(fix)
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun FixCard(
    fix: Fix
) {
    val stateContainerColor = when (fix.estado) {
        FixState.PENDING -> FixesColors.pendingContainer
        FixState.IN_PROGRESS -> FixesColors.inProgressContainer
        else -> FixesColors.fixedContainer
    }

    val stateContentColor = MaterialTheme.colorScheme.scrim
    var showImageDialog by remember { mutableStateOf(false) }
    var isImageLoading by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Column(modifier = Modifier.background(Color.White)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(stateContainerColor)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (fix.estado) {
                        FixState.PENDING -> "Pendiente"
                        FixState.IN_PROGRESS -> "En curso"
                        else -> "Arreglado"
                    },
                    color = stateContentColor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (fix.imagen.url.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(180.dp).padding(8.dp).clip(MaterialTheme.shapes.extraSmall)) {
                    if (isImageLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    AsyncImage(
                        model = fix.imagen.url,
                        contentDescription = "Imagen del arreglo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clickable { showImageDialog = true },
                        contentScale = ContentScale.Crop,
                        onSuccess = { isImageLoading = false },
                        onError = { isImageLoading = false }
                    )
                }
            }

            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painterResource(R.drawable.notes_24px), contentDescription = null, tint = stateContentColor.copy(0.5f))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = fix.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = stateContentColor
                )
            }

            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = stateContentColor.copy(0.5f))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = fix.localizacion, color = stateContentColor, style = MaterialTheme.typography.bodyMedium)
            }

            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.DateRange, contentDescription = null, tint = stateContentColor.copy(0.5f))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy, HH:mm").format(fix.fecha.toDate()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = stateContentColor
                )
            }
        }
    }

    if (showImageDialog) {
        Dialog(onDismissRequest = { showImageDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showImageDialog = false },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = fix.imagen.url,
                    contentDescription = "Imagen ampliada",
                    modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.extraSmall),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }

}
