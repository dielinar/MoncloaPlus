package com.example.moncloaplus.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

enum class EventType { ACTIVIDAD_COLEGIAL, CLUBES_PROFESIONALES, DE_INTERES, NONE }

data class Event(
    var id: String = "",
    val titulo: String = "",
    val fecha: Timestamp = Timestamp.now(),
    val descripcion: String = "",
    val tipo: EventType = EventType.NONE,
    var cartel: EventImage = EventImage(),
    val ponentes: List<String> = emptyList(),

    @get:Exclude
    var owner: User? = null

) {

    data class EventImage(
        val nombreArchivo: String = "",
        val url: String = "",
        val path: String = "",
        val tamano: Long = 0
    )

}
