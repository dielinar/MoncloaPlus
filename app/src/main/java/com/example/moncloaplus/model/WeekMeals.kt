package com.example.moncloaplus.model

data class WeekMeals (
    val id: String = "", // ID del documento en Firestore (ej. "24-02-2025")
    val meals: Map<String, Map<String, String>> = emptyMap()
)
