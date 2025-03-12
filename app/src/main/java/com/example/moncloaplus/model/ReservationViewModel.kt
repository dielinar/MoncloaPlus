package com.example.moncloaplus.model

import com.example.moncloaplus.SnackbarManager
import com.example.moncloaplus.model.service.ReservationService
import com.example.moncloaplus.screens.PlusViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(
    private val reservationService: ReservationService
): PlusViewModel() {

    private val _date = MutableStateFlow(System.currentTimeMillis())
    val date = _date.asStateFlow()

    private val _startTime = MutableStateFlow(getDefaultStartTime())
    val startTime: StateFlow<Pair<Int, Int>> = _startTime.asStateFlow()

    private val _endTime = MutableStateFlow(getDefaultEndTime(_startTime.value.first, _startTime.value.second))
    val endTime: StateFlow<Pair<Int, Int>> = _endTime.asStateFlow()

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note.asStateFlow()

    private val _userReservations = MutableStateFlow<List<Reservation>>(emptyList())
    val userReservations: StateFlow<List<Reservation>> = _userReservations.asStateFlow()

    private val _reservationsOfType = MutableStateFlow<Map<Int, List<Reservation>>>(emptyMap())
    val reservationsOfType: StateFlow<Map<Int, List<Reservation>>> = _reservationsOfType.asStateFlow()

    init {
        fetchUserReservations()
    }

    fun updateDate(newDate: Long) { _date.value = newDate }
    fun updateStartTime(hour: Int, minute: Int) { _startTime.value = Pair(hour, minute) }
    fun updateEndTime(hour: Int, minute: Int) { _endTime.value = Pair(hour ,minute) }
    fun updateNote(newNote: String) { _note.value = newNote }

    fun saveReservation(type: ReservType) {
        launchCatching {
            val reservation = Reservation(
                id = "",
                inicio = getStartTimestamp(),
                final = getEndTimestamp(),
                nota = _note.value,
                tipo = type
            )
            reservationService.saveReservationData(reservation)
            SnackbarManager.showMessage("Reserva guardada correctamente.")
            fetchUserReservations()
            fetchAllReservationsOfType(type.ordinal)
        }
    }

    private fun fetchUserReservations() {
        launchCatching {
            val reservationList = reservationService.getUserReservations()
            _userReservations.value = reservationList
        }
    }

    fun fetchAllReservationsOfType(index: Int) {
        launchCatching {
            val reservationList = reservationService.getAllReservationsOfType(index)
            _reservationsOfType.value = _reservationsOfType.value.toMutableMap().apply {
                this[index] = reservationList
            }
        }
    }

    private fun getStartTimestamp(): Timestamp {
        val date = _date.value
        val (hour, minute) = _startTime.value

        val calendar = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        return Timestamp(calendar.time)
    }

    private fun getEndTimestamp(): Timestamp {
        val date = _date.value
        val (hour, minute) = _endTime.value

        val calendar = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        return Timestamp(calendar.time)
    }

    private fun getDefaultStartTime(): Pair<Int, Int> {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        return if (currentMinute < 30) {
            currentHour to 30
        } else {
            val nextHour = (currentHour + 1) % 24
            nextHour to 0
        }
    }

    private fun getDefaultEndTime(startHour: Int, startMinute: Int): Pair<Int, Int> {
        val endHour = (startHour + 1) % 24
        return endHour to startMinute
    }

}
