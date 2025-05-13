package com.example.moncloaplus.service

import android.net.Uri
import com.example.moncloaplus.data.model.Event

interface EventService {
    suspend fun createEvent(event: Event, imageUri: Uri?): Event
    suspend fun getAllEventsByType(type: Int): List<Event>
    suspend fun addParticipant(eventId: String, userId: String)
    suspend fun removeParticipant(eventId: String, userId: String)
}
