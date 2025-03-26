package com.example.moncloaplus.screens.fixes

import androidx.compose.ui.graphics.Color
import com.example.moncloaplus.R

const val PENDING_INDEX = 0
const val IN_PROGRESS_INDEX = 1
const val FIXED_INDEX = 2

val FIXES_STATES_OPTIONS: List<String> = listOf("Pendientes", "En curso", "Arreglados")

val PENDING_ICON = R.drawable.stop_circle_24px
val IN_PROGRESS_ICON = R.drawable.hourglass_top_24px
val FIXED_ICON = R.drawable.check_24px

val FIXES_ICONS: List<Int> = listOf(PENDING_ICON, IN_PROGRESS_ICON, FIXED_ICON)

val FIXES_CONTAINER_COLORS: List<Color> = listOf(FixesColors.pendingContainer, FixesColors.inProgressContainer, FixesColors.fixedContainer)
val FIXES_CONTENT_COLORS: List<Color> = listOf()