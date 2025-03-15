package com.example.moncloaplus.model.service

import com.example.moncloaplus.model.Reservation

interface ReservationService {
    suspend fun createReservation(reservation: Reservation)
    suspend fun deleteReservation(reservationId: String)
    suspend fun getUserReservations(type: Int, dateMillis: Long): List<Reservation>
    suspend fun getReservationsOnDay(type: Int, dateMillis: Long): List<Reservation>
}
