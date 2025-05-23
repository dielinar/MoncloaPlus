package com.example.moncloaplus.data.model

enum class UserRole {
    NORMAL, VOCAL, ADMIN, DIRECCION, ESTUDIOS, DECANATO, CLUB, GERENCIA, MANTENIMIENTO
}

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

    fun isAdmin(): Boolean {
        return role == UserRole.ADMIN || role == UserRole.GERENCIA
    }

    fun isMaintainer(): Boolean {
        return role == UserRole.MANTENIMIENTO
    }

    fun canCreateFixes(): Boolean {
        return role != UserRole.MANTENIMIENTO
    }

    fun canAddEvents(): Boolean {
        return role == UserRole.VOCAL || role == UserRole.ESTUDIOS || role == UserRole.CLUB
    }

}
