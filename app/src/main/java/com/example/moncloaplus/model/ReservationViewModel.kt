package com.example.moncloaplus.model

import com.example.moncloaplus.SnackbarManager
import com.example.moncloaplus.model.service.ReservationService
import com.example.moncloaplus.model.service.impl.AccountServiceImpl
import com.example.moncloaplus.model.service.impl.StorageServiceImpl
import com.example.moncloaplus.screens.PlusViewModel
import com.example.moncloaplus.screens.reservation.getDefaultEndTime
import com.example.moncloaplus.screens.reservation.getDefaultStartTime
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(
    private val reservationService: ReservationService,
    private val accountService: AccountServiceImpl,
    private val storageService: StorageServiceImpl
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

    private val _userReservations = MutableStateFlow<Map<Int, List<Reservation>>>(emptyMap())
    val userReservations: StateFlow<Map<Int, List<Reservation>>> = _userReservations.asStateFlow()

    private val _reservationsOnDay = MutableStateFlow<List<Reservation>>(emptyList())
    val reservationsOnDay: StateFlow<List<Reservation>> = _reservationsOnDay.asStateFlow()

    fun updateNewDate(newDate: Long) { _newDate.value = newDate }
    fun updateCurrentDate(type: Int, newCurrentDay: Long) {
        _currentDate.value = newCurrentDay
        _newDate.value = newCurrentDay
        fetchReservationsOnDay(type, newCurrentDay)
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
            val currentUser = storageService.getUser(accountService.currentUserId)

            val reservation = Reservation(
                id = "",
                inicio = getStartTimestamp(),
                final = getEndTimestamp(),
                nota = _note.value,
                tipo = type,
                owner = currentUser
            )
            reservationService.createReservation(reservation)

            SnackbarManager.showMessage("Reserva creada correctamente.")

            addToUserReservations(reservation)
            addToReservationsOnDay(reservation)

            // Resetear valores
            updateNewDate(System.currentTimeMillis())
            updateStartTime(getDefaultStartTime())
            updateEndTime(getDefaultEndTime(_startTime.value))
            updateNote("")
        }
    }

    fun deleteReservation(reservationId: String) {
        launchCatching {
            reservationService.deleteReservation(reservationId)
            SnackbarManager.showMessage("Reserva eliminada correctamente.")

            removeFromUserReservations(reservationId)
            removeFromReservationsOnDay(reservationId)
        }
    }

    fun fetchUserReservations(type: Int, dateMillis: Long) {
        launchCatching {
            val reservationList = reservationService.getUserReservations(type, dateMillis)
            _userReservations.value = _userReservations.value.toMutableMap().apply {
                this[type] = reservationList
            }
        }
    }

    fun fetchReservationsOnDay(type: Int, dateMillis: Long) {
        launchCatching {
            val reservationList = reservationService.getReservationsOnDay(type, dateMillis)
            _reservationsOnDay.value = reservationList
        }
    }

    private fun addToUserReservations(reservation: Reservation) {
        val key = reservation.tipo.ordinal
        val currentList = _userReservations.value[key] ?: emptyList()
        val newList = (currentList + reservation).sortedBy { it.inicio }
        _userReservations.value = _userReservations.value.toMutableMap().apply {
            put(key, newList)
        }
    }

    private fun removeFromUserReservations(reservationId: String) {
        _userReservations.value = _userReservations.value.toMutableMap().apply {
            forEach { (key, list) ->
                put(key, list.filterNot { it.id == reservationId })
            }
        }
    }

    private fun addToReservationsOnDay(reservation: Reservation) {
        val currentList = _reservationsOnDay.value
        val newList = (currentList + reservation).sortedBy { it.inicio }
        _reservationsOnDay.value = newList
    }

    private fun removeFromReservationsOnDay(reservationId: String) {
        _reservationsOnDay.value = _reservationsOnDay.value.filterNot { it.id == reservationId }
    }

    private fun getStartTimestamp(): Timestamp {
        val date = _newDate.value
        val (hour, minute) = _startTime.value

        val calendar = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
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

            if (hour == 0 && minute == 0) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        return Timestamp(calendar.time)
    }


}
