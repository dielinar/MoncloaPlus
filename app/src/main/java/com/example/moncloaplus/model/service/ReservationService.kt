package com.example.moncloaplus.model.service

import com.example.moncloaplus.model.Reservation

interface ReservationService {
    suspend fun createReservation(reservation: Reservation): Reservation
    suspend fun deleteReservation(reservationId: String)
    suspend fun adminDelete(userId: String, reservationId: String)
    suspend fun editReservation(reservation: Reservation)
    suspend fun adminEdit(reservation: Reservation)
    suspend fun getReservation(reservationId: String): Reservation?
    suspend fun adminGetReservation(userId: String, reservationId: String): Reservation?
    suspend fun getUserReservations(type: Int, dateMillis: Long): List<Reservation>
    suspend fun getReservationsOnDay(type: Int, dateMillis: Long): List<Reservation>
    suspend fun addParticipant(reservation: Reservation, participantId: String)
    suspend fun deleteParticipant(reservation: Reservation, participantId: String)
}
