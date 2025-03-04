package com.example.moncloaplus.model

enum class UserRole {
    NORMAL, VOCAL, ADMIN, DIRECCION, ESTUDIOS, DECANATO, CLUB, GERENCIA
}

/**
 * https://firebase.google.com/docs/firestore/query-data/queries?hl=es&authuser=0&_gl=1*5helan*_ga*ODQ4OTg1NzUwLjE3Mzc0NTczOTc.*_ga_CW55HF8NVT*MTczOTM2NzI1MC4xOC4xLjE3MzkzNjczOTUuMjguMC4w#kotlin+ktx
 */

data class User (
    val id: String = "",
    val email: String = "",
    val provider: String = "",
    val firstName: String = "",
    val firstSurname: String = "",
    val secondSurname: String = "",
    val displayName: String = "",
    val initials: String = "",
    val roomNumber: String = "",
    val city: String = "",
    val degree: String = "",
    val university: String = "",
    val role: UserRole = UserRole.NORMAL
) {

    fun canExtractMeals(): Boolean {
        return role == UserRole.ADMIN || role == UserRole.GERENCIA
    }

    fun isAdmin(): Boolean {
        return role == UserRole.ADMIN || role == UserRole.GERENCIA
    }

}
