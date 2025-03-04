package com.example.moncloaplus.screens.meals

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

val WEEK_DAYS: List<String> = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
const val DATE_PATTERN: String = "dd-MM-yyyy"

@RequiresApi(Build.VERSION_CODES.O)
fun getExactDate(weekStartDate: String, dayName: String): String {
    val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN, Locale.getDefault())
    val startDate = LocalDate.parse(weekStartDate, formatter)

    val dayIndex = WEEK_DAYS.indexOf(dayName)
    if (dayIndex == -1) throw IllegalArgumentException("Día inválido: $dayName")

    val exactDate = startDate.plusDays(dayIndex.toLong())

    return exactDate.format(formatter)
}
