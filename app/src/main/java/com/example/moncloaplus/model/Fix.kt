package com.example.moncloaplus.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

enum class FixState { PENDING, IN_PROGRESS, FIXED }

data class Fix (
    var id: String = "",
    val fecha: Timestamp = Timestamp.now(),
    val localizacion: String = "",
    val descripcion: String = "",
    val estado: FixState = FixState.PENDING,
    var imagen: FixImage = FixImage(),

    @get:Exclude
    var owner: User? = null
) {
    data class FixImage(
        val nombreArchivo: String = "",
        val url: String = "",
        val path: String = "",
        val tamano: Long = 0
    )
}
