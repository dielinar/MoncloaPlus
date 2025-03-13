package com.example.moncloaplus.model.service

import com.example.moncloaplus.model.Reservation

interface ReservationService {
    suspend fun saveReservationData(reservation: Reservation)
    suspend fun getUserReservations(index: Int): List<Reservation>
    suspend fun getAllReservationsOfType(index: Int): List<Reservation>
}
