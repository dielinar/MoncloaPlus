package com.example.moncloaplus.model

import android.net.Uri
import com.example.moncloaplus.SnackbarManager
import com.example.moncloaplus.model.service.AccountService
import com.example.moncloaplus.model.service.EventService
import com.example.moncloaplus.model.service.StorageService
import com.example.moncloaplus.screens.PlusViewModel
import com.example.moncloaplus.screens.reservation.getDefaultStartTime
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventService: EventService,
    private val accountService: AccountService,
    private val storageService: StorageService
): PlusViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _speakers = MutableStateFlow<List<String>>(emptyList())
    val speakers: StateFlow<List<String>> = _speakers.asStateFlow()

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri.asStateFlow()

    private val _date = MutableStateFlow(System.currentTimeMillis())
    val date = _date.asStateFlow()

    private val _eventTime = MutableStateFlow(getDefaultStartTime())
    val eventTime: StateFlow<Pair<Int, Int>> = _eventTime.asStateFlow()

    private val _allEvents = MutableStateFlow<Map<Int, List<Event>>>(emptyMap())
    val allEvents: StateFlow<Map<Int, List<Event>>> = _allEvents.asStateFlow()

    fun updateTitle(newTitle: String) { _title.value = newTitle }
    fun updateDescription(newDescription: String) { _description.value = newDescription }
    fun updateSpeakers(newSpeakers: List<String>) { _speakers.value = newSpeakers }
    fun updateImageUri(uri: Uri) { _imageUri.value = uri }
    fun updateDate(newDate: Long) { _date.value = newDate }
    fun updateEventTime(newEventTime: Pair<Int, Int>) { _eventTime.value = newEventTime }

    fun createEvent(type: EventType) {
        launchCatching {
            _isLoading.value = true

            val currentUser = storageService.getUser(accountService.currentUserId)

            val event = Event(
                id = "",
                titulo = _title.value,
                fecha = getDateTimestamp(),
                descripcion = _description.value,
                tipo = type,
                cartel = Event.EventImage(),
                ponentes = _speakers.value,
                owner = currentUser
            )
            val newEvent = eventService.createEvent(event, imageUri.value)

            addToEventsMap(newEvent)
            _isLoading.value = false
            resetValues()
            SnackbarManager.showMessage("Evento creado correctamente.")
        }
    }

    fun fetchAllEvents() {
        launchCatching {
            _isLoading.value = true

            val types = EventType.entries.filter { it != EventType.NONE }

            val deferredResults = types.map { type ->
                async {
                    type.ordinal to eventService.getAllEventsByType(type.ordinal)
                }
            }

            val allFetchedEvents = deferredResults.awaitAll().toMap()
            _allEvents.value = allFetchedEvents

            _isLoading.value = false
        }
    }

    private fun resetValues() {
        updateTitle("")
        updateDescription("")
        updateSpeakers(emptyList())
        updateDate(System.currentTimeMillis())
        updateEventTime(getDefaultStartTime())
        _imageUri.value = null
    }

    private fun addToEventsMap(event: Event) {
        val stateKey = event.tipo.ordinal
        _allEvents.value = _allEvents.value.toMutableMap().apply {
            val updatedEvents = get(stateKey)?.toMutableList() ?: mutableListOf()
            updatedEvents.add(event)
            updatedEvents.sortByDescending { it.fecha }
            put(stateKey, updatedEvents)
        }
    }

    private fun getDateTimestamp(): Timestamp {
        val date = _date.value
        val (hour, minute) = _eventTime.value

        val calendar = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return Timestamp(calendar.time)
    }

}
