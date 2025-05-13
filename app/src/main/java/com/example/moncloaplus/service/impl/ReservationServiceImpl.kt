package com.example.moncloaplus.service.impl

import android.icu.util.Calendar
import android.util.Log
import com.example.moncloaplus.data.model.ReservType
import com.example.moncloaplus.data.model.Reservation
import com.example.moncloaplus.service.ReservationService
import com.google.firebase.Timestamp
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

    override suspend fun createReservation(reservation: Reservation): Reservation {
        val docRef = reservationsCollection.add(reservation).await()
        val newReservation = reservation.copy(id = docRef.id)
        reservationsCollection.document(docRef.id).set(newReservation).await()
        return newReservation
    }

    override suspend fun deleteReservation(reservationId: String) {
        reservationsCollection.document(reservationId)
            .delete()
            .await()
    }

    override suspend fun adminDelete(userId: String, reservationId: String) {
        usersCollection.document(userId).collection("reservations")
            .document(reservationId)
            .delete()
            .await()
    }

    override suspend fun editReservation(reservation: Reservation) {
        reservationsCollection.document(reservation.id)
            .set(reservation)
            .await()
    }

    override suspend fun adminEdit(reservation: Reservation) {
        usersCollection.document(reservation.owner!!.id).collection("reservations")
            .document(reservation.id)
            .set(reservation)
            .await()
    }

    override suspend fun getReservation(reservationId: String): Reservation? {
        return try {
            val doc = reservationsCollection.document(reservationId).get().await()
            val reservation = doc.toObject(Reservation::class.java)?.copy(id = doc.id)

            reservation?.let {
                val userId = doc.reference.parent.parent?.id
                if (userId != null) {
                    val user = storageService.getUser(userId)
                    it.owner = user
                }
            }
            reservation
        } catch(e: Exception) {
            Log.e("Firestore", "Error al obtener la reserva", e)
            null
        }
    }

    override suspend fun adminGetReservation(userId: String, reservationId: String): Reservation? {
        return try {
            val doc = usersCollection.document(userId)
                .collection("reservations")
                .document(reservationId)
                .get()
                .await()
            val reservation = doc.toObject(Reservation::class.java)?.copy(id = doc.id)

            reservation?.let {
                val user = storageService.getUser(userId)
                it.owner = user
            }
            reservation
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener la reserva", e)
            null
        }
    }

    override suspend fun getUserReservations(type: Int, dateMillis: Long): List<Reservation> = coroutineScope {
        val calendarStart = Calendar.getInstance().apply {
            timeInMillis = dateMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calendarEnd = Calendar.getInstance().apply {
            timeInMillis = dateMillis
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        val startOfDayTimestamp = Timestamp(calendarStart.time)
        val endOfDayTimestamp = Timestamp(calendarEnd.time)
        try {
            val result = reservationsCollection
                .whereEqualTo("tipo", ReservType.entries[type].name)
                .whereGreaterThan("inicio", startOfDayTimestamp)
                .whereLessThan("inicio", endOfDayTimestamp)
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
                } else null
            }
            reservationsDeferred.awaitAll()
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener las reservas", e)
            emptyList()
        }
    }

    override suspend fun getReservationsOnDay(type: Int, dateMillis: Long): List<Reservation> = coroutineScope {
        val calendarStart = Calendar.getInstance().apply {
            timeInMillis = dateMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calendarEnd = Calendar.getInstance().apply {
            timeInMillis = dateMillis
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        val startOfDayTimestamp = Timestamp(calendarStart.time)
        val endOfDayTimestamp = Timestamp(calendarEnd.time)

        try {
            val result = db.collectionGroup("reservations")
                .whereEqualTo("tipo", ReservType.entries[type].name)
                .whereLessThan("inicio", endOfDayTimestamp)
                .whereGreaterThan("inicio", startOfDayTimestamp)
                .orderBy("inicio", Query.Direction.ASCENDING)
                .get()
                .await()

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
            emptyList()
        }
    }

    override suspend fun addParticipant(reservation: Reservation, participantId: String) {
        val ownerId = reservation.owner?.id ?: return
        val reservationRef = usersCollection.document(ownerId)
            .collection("reservations")
            .document(reservation.id)

        val currentParticipants = reservation.participantes.toMutableList()
        if (!currentParticipants.contains(participantId)) {
            currentParticipants.add(participantId)
            reservationRef.update("participantes", currentParticipants).await()
        }
    }

    override suspend fun deleteParticipant(reservation: Reservation, participantId: String) {
        val ownerId = reservation.owner?.id ?: return
        val reservationRef = usersCollection.document(ownerId)
            .collection("reservations")
            .document(reservation.id)

        val updatedParticipants = reservation.participantes.filterNot { it == participantId }
        reservationRef.update("participantes", updatedParticipants).await()
    }

}
