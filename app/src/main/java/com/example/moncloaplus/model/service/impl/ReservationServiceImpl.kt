package com.example.moncloaplus.model.service.impl

import android.util.Log
import com.example.moncloaplus.model.ReservType
import com.example.moncloaplus.model.Reservation
import com.example.moncloaplus.model.service.ReservationService
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReservationServiceImpl @Inject constructor(
    private val db: FirebaseFirestore,
    accService: AccountServiceImpl,
    private val storageService: StorageServiceImpl
): ReservationService {

    private val userId = accService.currentUserId

    private val usersCollection = db.collection("users")

    private val reservationsCollection: CollectionReference
        get() = userId.let { usersCollection.document(it).collection("reservations") }

    override suspend fun createReservation(reservation: Reservation) {
        reservationsCollection
            .add(reservation)
            .await()
    }

    override suspend fun deleteReservation(reservationId: String) {
        reservationsCollection.document(reservationId)
            .delete()
            .await()
    }

    override suspend fun getUserReservations(type: Int): List<Reservation> = coroutineScope {
        try {
            val result = reservationsCollection
                .whereEqualTo("tipo", ReservType.entries[type].name)
                .orderBy("inicio", Query.Direction.ASCENDING)
                .get().await()

            val reservationsDeferred = result.documents.mapNotNull { doc ->
                val reservation = doc.toObject(Reservation::class.java)?.copy(id = doc.id)
                val userId = doc.reference.parent.parent?.id
                if (reservation != null && userId != null) {
                    async {
                        val user = storageService.getUser(userId)
                        reservation.owner = user
                        reservation
                    }
                } else {
                    null
                }
            }
            reservationsDeferred.awaitAll()
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener las reservas", e)
            emptyList()
        }
    }

    override suspend fun getAllReservationsOfType(type: Int): List<Reservation> = coroutineScope {
        try {
            val result = db.collectionGroup("reservations")
                .whereEqualTo("tipo", ReservType.entries[type].name)
                .orderBy("inicio", Query.Direction.ASCENDING)
                .get().await()

            val reservationsDeferred = result.documents.mapNotNull { doc ->
                val reservation = doc.toObject(Reservation::class.java)?.copy(id = doc.id)
                val userId = doc.reference.parent.parent?.id
                if (reservation != null && userId != null) {
                    async {
                        val user = storageService.getUser(userId)
                        reservation.owner = user
                        reservation
                    }
                } else {
                    null
                }
            }
            reservationsDeferred.awaitAll()
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener las reservas de ${ReservType.entries[type]}", e)
            emptyList()
        }
    }

}
