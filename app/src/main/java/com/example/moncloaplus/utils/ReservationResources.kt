package com.example.moncloaplus.utils

import com.example.moncloaplus.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

val RESERVATION_OPTIONS: List<String> = listOf("Pádel", "Gimnasio", "Estudio", "Piano")

val PADEL_ICON = R.drawable.sports_tennis_24px
val GYM_ICON = R.drawable.fitness_center_24px
val ESTUDIO_ICON = R.drawable.headphones_24px
val PIANO_ICON = R.drawable.piano_24px

fun Long.toFormattedDate(): String {
    val calendar = Calendar.getInstance().apply { timeInMillis = this@toFormattedDate }
    val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale("es", "ES"))
    return sdf.format(calendar.time).replaceFirstChar { it.uppercaseChar() }
}

fun formatHourMinute(hour: Int, minute: Int): String {
    return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
}
