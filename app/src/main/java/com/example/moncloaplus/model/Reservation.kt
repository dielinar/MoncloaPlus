package com.example.moncloaplus.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

enum class ReservType { PADEL, GYM, ESTUDIO, PIANO, NONE }

data class Reservation (
    var id: String = "",
    val inicio: Timestamp = Timestamp.now(),
    val final: Timestamp = Timestamp.now(),
    val nota: String = "",
    val tipo: ReservType = ReservType.NONE,

    @get:Exclude
    var owner: User? = null
)
