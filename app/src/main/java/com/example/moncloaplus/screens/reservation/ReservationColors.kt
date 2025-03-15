package com.example.moncloaplus.screens.reservation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object ReservationColors {

    val greenContainer = Color(0xFFd9f2d4)
    val greenContent = Color(0xFF2c6328)
    val redContainer = Color(0xFFffd0d0)
    val redContent = Color(0xFF7E2117)

    @Composable
    fun pastContainer() = MaterialTheme.colorScheme.surfaceVariant.copy(0.8f)
    @Composable
    fun pastContent() = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f)

}
