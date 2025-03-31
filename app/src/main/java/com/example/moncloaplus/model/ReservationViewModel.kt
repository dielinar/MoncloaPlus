package com.example.moncloaplus.model

import com.example.moncloaplus.SnackbarManager
import com.example.moncloaplus.model.service.AccountService
import com.example.moncloaplus.model.service.ReservationService
import com.example.moncloaplus.model.service.StorageService
import com.example.moncloaplus.model.service.impl.AccountServiceImpl
import com.example.moncloaplus.model.service.impl.StorageServiceImpl
import com.example.moncloaplus.screens.PlusViewModel
import com.example.moncloaplus.screens.reservation.getDefaultEndTime
import com.example.moncloaplus.screens.reservation.getDefaultStartTime
import com.example.moncloaplus.screens.reservation.normalizeDate
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(
    private val reservationService: ReservationService,
    private val accountService: AccountService,
    private val storageService: StorageService
): PlusViewModel() {

    private val _newDate = MutableStateFlow(System.currentTimeMillis())
    val newDate = _newDate.asStateFlow()

    private val _currentDate = MutableStateFlow(System.currentTimeMillis())
    val currentDate = _currentDate.asStateFlow()

    private val _startTime = MutableStateFlow(getDefaultStartTime())
    val startTime: StateFlow<Pair<Int, Int>> = _startTime.asStateFlow()

    private val _endTime = MutableStateFlow(getDefaultEndTime(_startTime.value))
    val endTime: StateFlow<Pair<Int, Int>> = _endTime.asStateFlow()

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note.asStateFlow()

    private val _editingReservation = MutableStateFlow<Reservation?>(null)
    val editingReservation = _editingReservation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _userReservations = MutableStateFlow<Map<Int, Map<Long, List<Reservation>>>>(emptyMap())
    val userReservations: StateFlow<Map<Int, Map<Long, List<Reservation>>>> = _userReservations.asStateFlow()

    private val _reservationsByDate = MutableStateFlow<Map<Int, Map<Long, List<Reservation>>>>(emptyMap())
    val reservationsByDate: StateFlow<Map<Int, Map<Long, List<Reservation>>>> = _reservationsByDate.asStateFlow()

    private val _participantsMap = MutableStateFlow<Map<String, List<User>>>(emptyMap())
    val participantsMap: StateFlow<Map<String, List<User>>> = _participantsMap.asStateFlow()

    init {
        val today = normalizeDate(System.currentTimeMillis())

        ReservType.entries.forEach { type ->
            fetchReservationsByDate(type.ordinal, today)
            fetchUserReservations(type.ordinal, today)
        }
    }

    fun updateNewDate(newDate: Long) { _newDate.value = newDate }
    fun updateCurrentDate(type: Int, newCurrentDay: Long) {
        _currentDate.value = newCurrentDay
        _newDate.value = newCurrentDay
        fetchReservationsByDate(type, newCurrentDay)
        fetchUserReservations(type, newCurrentDay)
    }
    fun updateStartTime(newStartTime: Pair<Int, Int>) {
        _startTime.value = newStartTime
        _endTime.value = Pair((newStartTime.first + 1) % 24, newStartTime.second)
    }
    fun updateEndTime(newEndTime: Pair<Int, Int>) { _endTime.value = newEndTime }
    fun updateNote(newNote: String) { _note.value = newNote }

    fun createReservation(type: ReservType) {
        launchCatching {
            _isLoading.value = true

            val currentUser = storageService.getUser(accountService.currentUserId)
            val participantsList = if (type == ReservType.GYM) listOf(currentUser!!.id) else emptyList()

            val reservation = Reservation(
                id = "",
                inicio = getStartTimestamp(),
                final = getEndTimestamp(),
                nota = _note.value,
                tipo = type,
                participantes = participantsList,
                owner = currentUser,
            )
            val newReservation = reservationService.createReservation(reservation)

            SnackbarManager.showMessage("Reserva creada correctamente.")

            addToUserReservations(newReservation)
            addToReservationsByDate(newReservation)
            addParticipant(newReservation, currentUser!!.id)

            resetValues()

            _isLoading.value = false
        }
    }

    fun resetValues() {
        updateNewDate(_currentDate.value)
        updateStartTime(getDefaultStartTime())
        updateEndTime(getDefaultEndTime(_startTime.value))
        updateNote("")
        _editingReservation.value = null
    }

    fun deleteReservation(reservation: Reservation) {
        launchCatching {
            _isLoading.value = true
            reservationService.deleteReservation(reservation.id)
            SnackbarManager.showMessage("Reserva eliminada correctamente.")

            removeFromUserReservations(reservation.id)
            removeFromReservationsByDate(reservation.id, reservation.tipo.ordinal)
            _isLoading.value = false
        }
    }

    fun adminDelete(reservation: Reservation) {
        launchCatching {
            _isLoading.value = true
            reservationService.adminDelete(reservation.owner!!.id, reservation.id)
            SnackbarManager.showMessage("Reserva eliminada correctamente.")

            removeFromUserReservations(reservation.id)
            removeFromReservationsByDate(reservation.id, reservation.tipo.ordinal)
            _isLoading.value = false
        }
    }

    fun fetchUserReservations(type: Int, dateMillis: Long) {
        launchCatching {
            _isLoading.value = true
            val reservationList = reservationService.getUserReservations(type, dateMillis)
            val sortedList = reservationList.sortedBy { it.inicio }
            val normalizedMap = sortedList.groupBy { normalizeDate(it.inicio.toDate().time) }

            val normalizedDate = normalizeDate(dateMillis)
            val updatedMap = normalizedMap.toMutableMap().apply {
                if (this[normalizedDate] == null) {
                    put(normalizedDate, emptyList())
                }
            }

            _userReservations.value = _userReservations.value.toMutableMap().apply {
                put(type, updatedMap)
            }
            _isLoading.value = false
        }
    }

    fun fetchReservationsByDate(type: Int, dateMillis: Long) {
        launchCatching {
            _isLoading.value = true
            val normalizedDate = normalizeDate(dateMillis)
            val reservationList = reservationService.getReservationsOnDay(type, dateMillis)
            _reservationsByDate.value = _reservationsByDate.value.toMutableMap().apply {
                val updatedTypeReservations = get(type)?.toMutableMap() ?: mutableMapOf()
                updatedTypeReservations[normalizedDate] = reservationList
                put(type, updatedTypeReservations)
            }
            reservationList.forEach { reservation ->
                fetchParticipants(reservation)
            }
            _isLoading.value = false
        }
    }

    fun adminLoadReservationForEditing(userId: String, reservationId: String) {
        launchCatching {
            val reservation = reservationService.adminGetReservation(userId, reservationId)
            reservation?.let {
                _editingReservation.value = it

                _newDate.value = it.inicio.toDate().time

                val calendar = Calendar.getInstance()
                calendar.time = it.inicio.toDate()
                _startTime.value = Pair(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))

                calendar.time = it.final.toDate()
                _endTime.value = Pair(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))

                _note.value = it.nota
            }
        }
    }

    fun loadReservationForEditing(reservationId: String) {
        launchCatching {
            val reservation = reservationService.getReservation(reservationId)
            reservation?.let {
                _editingReservation.value = it

                _newDate.value = it.inicio.toDate().time

                val calendar = Calendar.getInstance()
                calendar.time = it.inicio.toDate()
                _startTime.value = Pair(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))

                calendar.time = it.final.toDate()
                _endTime.value = Pair(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))

                _note.value = it.nota
            }
        }
    }

    fun editReservation() {
        launchCatching {
            _editingReservation.value?.let { original ->
                val updatedReservation = original.copy(
                    inicio = getStartTimestamp(),
                    final = getEndTimestamp(),
                    nota = _note.value
                )
                reservationService.editReservation(updatedReservation)
                SnackbarManager.showMessage("Reserva editada correctamente.")
            }

            _editingReservation.value?.tipo?.let { _editingReservation.value?.inicio?.toDate()
                ?.let { it1 -> fetchUserReservations(it.ordinal, it1.time) } }
            _editingReservation.value?.tipo?.let { _editingReservation.value?.inicio?.toDate()
                ?.let { it1 -> fetchReservationsByDate(it.ordinal, it1.time) } }

            resetValues()

            _editingReservation.value = null
        }
    }

    fun adminEdit() {
        launchCatching {
            _editingReservation.value?.let { original ->
                val updatedReservation = original.copy(
                    inicio = getStartTimestamp(),
                    final = getEndTimestamp(),
                    nota = _note.value
                )
                reservationService.adminEdit(updatedReservation)
                SnackbarManager.showMessage("Reserva editada correctamente.")
            }

            _editingReservation.value?.tipo?.let { _editingReservation.value?.inicio?.toDate()
                ?.let { it1 -> fetchUserReservations(it.ordinal, it1.time) } }
            _editingReservation.value?.tipo?.let { _editingReservation.value?.inicio?.toDate()
                ?.let { it1 -> fetchReservationsByDate(it.ordinal, it1.time) } }

            resetValues()

            _editingReservation.value = null
        }
    }

    fun getValidationError(type: Int): String? {
        val startTimestamp = getStartTimestamp()
        val endTimestamp = getEndTimestamp()
        val now = Timestamp.now()

        return when {
            isReservationOverlap(type) -> "La reserva solapa con otra existente."

            !startTimestamp.toDate().after(now.toDate()) -> "La hora de inicio debe ser posterior a la actual."

            !endTimestamp.toDate().after(startTimestamp.toDate()) -> "La hora final debe ser mayor que la de inicio."

            else -> {
                val startCalendar = Calendar.getInstance().apply { time = startTimestamp.toDate() }
                val endCalendar = Calendar.getInstance().apply { time = endTimestamp.toDate() }
                if (startCalendar.get(Calendar.YEAR) != endCalendar.get(Calendar.YEAR) ||
                    startCalendar.get(Calendar.MONTH) != endCalendar.get(Calendar.MONTH) ||
                    startCalendar.get(Calendar.DAY_OF_MONTH) != endCalendar.get(Calendar.DAY_OF_MONTH)
                ) {
                    "La reserva no puede terminar en otro día."
                } else null
            }
        }
    }

    fun addParticipant(reservation: Reservation, participantId: String) {
        launchCatching {
            reservationService.addParticipant(reservation, participantId)
            val newParticipant = storageService.getUser(participantId)!!
            _participantsMap.update { currentMap ->
                val currentList = currentMap[reservation.id] ?: emptyList()
                currentMap.toMutableMap().apply {
                    put(reservation.id, currentList + newParticipant)
                }
            }
        }
    }

    fun deleteParticipant(reservation: Reservation, participantId: String) {
        launchCatching {
            reservationService.deleteParticipant(reservation, participantId)
            _participantsMap.update { currentMap ->
                val currentList = currentMap[reservation.id] ?: emptyList()
                currentMap.toMutableMap().apply {
                    put(reservation.id, currentList.filterNot { it.id == participantId })
                }
            }
        }
    }

    private fun fetchParticipants(reservation: Reservation) {
        launchCatching {
            val users = reservation.participantes.mapNotNull { userId ->
                storageService.getUser(userId)
            }
            _participantsMap.update { currentMap ->
                currentMap.toMutableMap().apply {
                    put(reservation.id, users)
                }
            }
        }
    }

    private fun isReservationOverlap(type: Int): Boolean {
        val normalizedDate = normalizeDate(_newDate.value)
        val reservationsToday = _reservationsByDate.value[type]?.get(normalizedDate) ?: emptyList()
        val newStart = getStartTimestamp()
        val newEnd = getEndTimestamp()

        val editingReservation = _editingReservation.value

        val filteredReservations = if (editingReservation != null) {
            reservationsToday.filter { it.id != editingReservation.id }
        } else {
            reservationsToday
        }

        return filteredReservations.any { existing ->
            newStart < existing.final && newEnd > existing.inicio
        }
    }

    private fun addToUserReservations(reservation: Reservation) {
        val typeKey = reservation.tipo.ordinal
        val normalizedDate = normalizeDate(reservation.inicio.toDate().time)
        _userReservations.value = _userReservations.value.toMutableMap().apply {
            val updatedTypeReservations = get(typeKey)?.toMutableMap() ?: mutableMapOf()
            val currentList = updatedTypeReservations[normalizedDate] ?: emptyList()
            val newList = (currentList + reservation).sortedBy { it.inicio }
            updatedTypeReservations[normalizedDate] = newList
            put(typeKey, updatedTypeReservations)
        }
    }

    private fun removeFromUserReservations(reservationId: String) {
        _userReservations.value = _userReservations.value.toMutableMap().apply {
            forEach { (type, reservationsByDate) ->
                val updatedReservationsByDate = reservationsByDate.toMutableMap()
                updatedReservationsByDate.keys.forEach { date ->
                    updatedReservationsByDate[date] = updatedReservationsByDate[date]?.filterNot { it.id == reservationId } ?: emptyList()
                }
                put(type, updatedReservationsByDate)
            }
        }
    }

    private fun addToReservationsByDate(reservation: Reservation) {
        val typeKey = reservation.tipo.ordinal
        val normalizedDate = normalizeDate(reservation.inicio.toDate().time)

        _reservationsByDate.value = _reservationsByDate.value.toMutableMap().apply {
            val updatedTypeReservations = get(typeKey)?.toMutableMap() ?: mutableMapOf()
            val currentList = updatedTypeReservations[normalizedDate] ?: emptyList()
            val newList = (currentList + reservation).sortedBy { it.inicio }
            updatedTypeReservations[normalizedDate] = newList
            put(typeKey, updatedTypeReservations)
        }
    }

    private fun removeFromReservationsByDate(reservationId: String, type: Int) {
        _reservationsByDate.value = _reservationsByDate.value.toMutableMap().apply {
            val updatedTypeReservations = get(type)?.toMutableMap() ?: return@apply
            updatedTypeReservations.keys.forEach { date ->
                updatedTypeReservations[date] = updatedTypeReservations[date]?.filterNot { it.id == reservationId } ?: emptyList()
            }
            put(type, updatedTypeReservations)
        }
    }

    private fun getStartTimestamp(): Timestamp {
        val date = _newDate.value
        val (hour, minute) = _startTime.value

        val calendar = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return Timestamp(calendar.time)
    }

    private fun getEndTimestamp(): Timestamp {
        val date = _newDate.value
        val (hour, minute) = _endTime.value

        val calendar = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (hour == 0 && minute == 0) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        return Timestamp(calendar.time)
    }

}
