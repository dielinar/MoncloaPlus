package com.example.moncloaplus.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

enum class ReservType {
    PADEL, GYM, ESTUDIO, PIANO
}

data class Reservation (
    val id: String = "", // clave aleatoria
    val inicio: Timestamp,
    val final: Timestamp,
    val nota: String = "",
    val tipo: ReservType
) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertTimestampToLocalDateTime(timestamp: Timestamp): LocalDateTime {
        return timestamp.toDate().toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun localDateTimeToTimestamp(dateTime: LocalDateTime): Timestamp {
        val instant = dateTime.atZone(ZoneId.systemDefault()).toInstant()
        return Timestamp(Date.from(instant))
    }

}
