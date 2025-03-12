package com.example.moncloaplus.model.service.impl

import android.util.Log
import com.example.moncloaplus.model.ReservType
import com.example.moncloaplus.model.Reservation
import com.example.moncloaplus.model.service.ReservationService
import com.example.moncloaplus.screens.reservation.RESERVATION_NAMES
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReservationServiceImpl @Inject constructor(
    private val db: FirebaseFirestore,
    accService: AccountServiceImpl
): ReservationService {

    private val userId = accService.currentUserId

    private val usersCollection = db.collection("users")

    private val reservationsCollection: CollectionReference
        get() = userId.let { usersCollection.document(it).collection("reservations") }

    override suspend fun saveReservationData(reservation: Reservation) {
        reservationsCollection
            .add(reservation)
            .await()
    }

    override suspend fun getUserReservations(): List<Reservation> {
        return try {
            val result = reservationsCollection.get().await()
            result.documents.mapNotNull { doc ->
                doc.toObject(Reservation::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener las reservas", e)
            emptyList()
        }
    }

    override suspend fun getAllReservationsOfType(index: Int): List<Reservation> {
        return try {
            val result = db.collectionGroup("reservations")
                .whereEqualTo("tipo", ReservType.entries[index].name)
                .orderBy("tipo", Query.Direction.ASCENDING)
                .get().await()

            result.documents.mapNotNull { doc ->
                doc.toObject(Reservation::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener las reservas de ${ReservType.entries[index]}", e)
            emptyList()
        }
    }


}
