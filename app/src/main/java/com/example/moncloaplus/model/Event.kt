package com.example.moncloaplus.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

enum class EventType { ACTIVIDAD_COLEGIAL, CLUBES_PROFESIONALES, DE_INTERES, NONE }

enum class ActividadesColegiales { TERTULIAS_INVITADO, CULTURA, SOLIDARIDAD, DEPORTES, FORMACION_CRISTIANA, EVENTOS }

enum class ClubesProfesionales { MEDICINA, EMPRESA, DERECHO, INGENIERIA }

data class Event(
    var id: String = "",
    val titulo: String = "",
    val fecha: Timestamp = Timestamp.now(),
    val descripcion: String = "",
    val tipo: EventType = EventType.NONE,
    val subtipo: String = "",
    var cartel: EventImage = EventImage(),
    val ponentes: List<String> = emptyList(),
    var asistentes: List<String> = emptyList(),

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
