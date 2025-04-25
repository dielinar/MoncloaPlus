package com.example.moncloaplus.model

import android.net.Uri
import com.example.moncloaplus.model.service.EventService
import com.example.moncloaplus.screens.PlusViewModel
import com.example.moncloaplus.screens.create_event.EVENT_TYPES
import com.example.moncloaplus.screens.reservation.getDefaultStartTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventService: EventService
): PlusViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _eventType = MutableStateFlow(EVENT_TYPES[0])
    val eventType: StateFlow<String> = _eventType.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _speakers = MutableStateFlow(listOf(""))
    val speakers: StateFlow<List<String>> = _speakers.asStateFlow()

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri.asStateFlow()

    private val _date = MutableStateFlow(System.currentTimeMillis())
    val date = _date.asStateFlow()

    private val _eventTime = MutableStateFlow(getDefaultStartTime())
    val eventTime: StateFlow<Pair<Int, Int>> = _eventTime.asStateFlow()

    fun updateTitle(newTitle: String) { _title.value = newTitle }
    fun updateEventType(newType: String) { _eventType.value = newType }
    fun updateDescription(newDescription: String) { _description.value = newDescription }
    fun updateSpeakers(newSpeakers: List<String>) { _speakers.value = newSpeakers }
    fun updateImageUri(uri: Uri) { _imageUri.value = uri }
    fun updateDate(newDate: Long) { _date.value = newDate }
    fun updateEventTime(newEventTime: Pair<Int, Int>) { _eventTime.value = newEventTime }

}
