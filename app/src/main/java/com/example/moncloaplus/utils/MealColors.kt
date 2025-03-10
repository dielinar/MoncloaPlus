package com.example.moncloaplus.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object MealColors {
    val prontoContainer = Color(0xFFD6F6D6)
    val tardeContainerDark = Color(0xFFE5B789)
    val tardeContainerLight = Color(0xFFFAD7BA)
    val noSelectionContainer = Color(0xFFE35353)

    @Composable
    fun breakfastContainer() = MaterialTheme.colorScheme.surfaceContainerLowest
    @Composable
    fun lunchContainer() = MaterialTheme.colorScheme.surfaceContainerLow
    @Composable
    fun dinnerContainer() = MaterialTheme.colorScheme.surfaceContainerHigh
    @Composable
    fun normalContainer() = MaterialTheme.colorScheme.primaryContainer
}