package com.example.moncloaplus.model.service.impl

import android.net.Uri
import android.util.Log
import com.example.moncloaplus.model.Event
import com.example.moncloaplus.model.EventType
import com.example.moncloaplus.model.UploadResult
import com.example.moncloaplus.model.service.EventService
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EventServiceImpl @Inject constructor(
    db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val storageService: StorageServiceImpl
): EventService {

    private val eventsCollection = db.collection("events")

    override suspend fun createEvent(event: Event, imageUri: Uri?): Event {
        val docRef = eventsCollection.add(event).await()
        val eventId = docRef.id
        var eventImage = Event.EventImage()

        if (imageUri != null) {
            val uploadResult = uploadImage(eventId, event.tipo, imageUri)
            eventImage = Event.EventImage(
                nombreArchivo = uploadResult.fileName,
                url = uploadResult.downloadUrl,
                path = uploadResult.path,
                tamano = uploadResult.size
            )
        }
        val newEvent = event.copy(id = eventId, cartel = eventImage)
        eventsCollection.document(docRef.id).set(newEvent).await()

        return newEvent
    }

    override suspend fun getAllEventsByType(type: Int): List<Event> = coroutineScope {
        try {
            val eventsSnapshot = eventsCollection
                .whereEqualTo("tipo", EventType.entries[type].name)
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .await()

            val allEventsDeferred = eventsSnapshot.documents.map { doc ->
                async {
                    doc.toObject(Event::class.java)?.copy(id = doc.id)?.also { event ->
                        val ownerId = event.owner?.id
                        if (ownerId != null) {
                            val user = storageService.getUser(ownerId)
                            event.owner = user
                        }
                        if (event.cartel.path.isNotEmpty()) {
                            event.cartel = event.cartel.copy(
                                url = getImageUrl(event.cartel.path)
                            )
                        }
                    }
                }
            }
            allEventsDeferred.awaitAll().filterNotNull()
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener eventos por tipo", e)
            emptyList()
        }
    }

    override suspend fun addParticipant(eventId: String, userId: String) {
        val eventDoc = eventsCollection.document(eventId)
        eventDoc.update("asistentes", FieldValue.arrayUnion(userId)).await()
    }

    override suspend fun removeParticipant(eventId: String, userId: String) {
        val eventDoc = eventsCollection.document(eventId)
        eventDoc.update("asistentes", FieldValue.arrayRemove(userId)).await()
    }

    private suspend fun uploadImage(eventId: String, eventType: EventType, uri: Uri): UploadResult {
        val typePath = when (eventType) {
            EventType.ACTIVIDAD_COLEGIAL -> "actividades_colegiales"
            EventType.CLUBES_PROFESIONALES -> "clubes_profesionales"
            EventType.DE_INTERES -> "de_interes"
            else -> "otros"
        }

        val storagePath = "events/$typePath/$eventId"
        val storageRef = storage.reference.child(storagePath)

        val uploadTask = storageRef.putFile(uri).await()
        val downloadUrl = storageRef.downloadUrl.await().toString()
        val metadata = uploadTask.metadata
        val size = metadata?.sizeBytes ?: 0

        return UploadResult(
            fileName = eventId,
            downloadUrl = downloadUrl,
            path = storagePath,
            size = size
        )
    }

    private suspend fun getImageUrl(imagePath: String): String {
        return try {
            val storageRef = storage.reference.child(imagePath)
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("Firebase Storage", "Error al obtener la URL de la imagen", e)
            ""
        }
    }

}
