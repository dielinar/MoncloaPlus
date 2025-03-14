package com.example.moncloaplus.model.service

import com.example.moncloaplus.model.Reservation

interface ReservationService {
    suspend fun createReservation(reservation: Reservation)
    suspend fun deleteReservation(reservationId: String)
    suspend fun getUserReservations(type: Int): List<Reservation>
    suspend fun getAllReservationsOfType(type: Int): List<Reservation>
}
