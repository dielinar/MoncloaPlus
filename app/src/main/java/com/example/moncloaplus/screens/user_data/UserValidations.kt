package com.example.moncloaplus.screens.user_data

private const val ROOM_PATTERN = "^([35])\\.([12345])\\.(1[0-9]|2[0-5]|[1-9])$"
private const val INITIALS_PATTERN = "^[A-Z]\\.[A-Z]\\.[A-Z]$"

fun String.isValidRoom(): Boolean {
    return this.matches(Regex(ROOM_PATTERN))
}

fun String.areValidInitials(): Boolean {
    return this.matches(Regex(INITIALS_PATTERN))
}