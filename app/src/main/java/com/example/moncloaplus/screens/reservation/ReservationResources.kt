package com.example.moncloaplus.screens.reservation

import com.example.moncloaplus.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

const val PADEL_INDEX = 0
const val GYM_INDEX = 1
const val MUSIC_STUDIO_INDEX = 2
const val PIANO_INDEX = 3

val RESERVATION_NAMES: List<String> = listOf("Pádel", "Gimnasio", "Estudio", "Piano")

val PADEL_ICON = R.drawable.sports_tennis_24px
val GYM_ICON = R.drawable.fitness_center_24px
val MUSIC_STUDIO_ICON = R.drawable.headphones_24px
val PIANO_ICON = R.drawable.piano_24px

val RESERVATION_ICONS: List<Int> = listOf(PADEL_ICON, GYM_ICON, MUSIC_STUDIO_ICON, PIANO_ICON)

fun Long.toFormattedDate(): String {
    val calendar = Calendar.getInstance().apply { timeInMillis = this@toFormattedDate }
    val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale("es", "ES"))
    return sdf.format(calendar.time).replaceFirstChar { it.uppercaseChar() }
}

fun formatHourMinute(hour: Int, minute: Int): String {
    return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
}
