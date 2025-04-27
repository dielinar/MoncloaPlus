package com.example.moncloaplus.model.service

import android.net.Uri
import com.example.moncloaplus.model.Event

interface EventService {
    suspend fun createEvent(event: Event, imageUri: Uri?): Event
    suspend fun getAllEventsByType(type: Int): List<Event>
    suspend fun addParticipant(eventId: String, userId: String)
    suspend fun removeParticipant(eventId: String, userId: String)
}
