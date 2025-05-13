package com.example.moncloaplus.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

const val MAX_GYM_PARTICIPANTS = 3

enum class ReservType { PADEL, GYM, ESTUDIO, PIANO, NONE }

data class Reservation (
    var id: String = "",
    val inicio: Timestamp = Timestamp.now(),
    val final: Timestamp = Timestamp.now(),
    val nota: String = "",
    val tipo: ReservType = ReservType.NONE,
    var participantes: List<String> = emptyList(),

    @get:Exclude
    var owner: User? = null,
)
