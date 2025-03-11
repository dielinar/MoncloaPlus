package com.example.moncloaplus.screens.reservation

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
): PlusViewModel() {

    private val _selectedDate = MutableStateFlow<Long?>(null)
    val selectedDate = _selectedDate.asStateFlow()

    private val _startTime = MutableStateFlow<Pair<Int, Int>?>(null)
    val startTime: StateFlow<Pair<Int, Int>?> = _startTime.asStateFlow()

    private val _endTime = MutableStateFlow<Pair<Int, Int>?>(null)
    val endTime: StateFlow<Pair<Int, Int>?> = _endTime.asStateFlow()

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note.asStateFlow()

    fun updateSelectedDate(newSelectedDate: Long?) { _selectedDate.value = newSelectedDate }
    fun updateStartTime(hour: Int, minute: Int) { _startTime.value = Pair(hour, minute) }
    fun updateEndTime(hour: Int, minute: Int) { _endTime.value = Pair(hour ,minute) }
    fun updateNote(newNote: String) { _note.value = newNote }

    fun getStartTimestamp(): Timestamp? {
        val date = _selectedDate.value ?: return null
        val (hour, minute) = _startTime.value ?: return null

        val calendar = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        return Timestamp(calendar.time)
    }

    fun getEndTimestamp(): Timestamp? {
        val date = _selectedDate.value ?: return null
        val (hour, minute) = _endTime.value ?: return null

        val calendar = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        return Timestamp(calendar.time)
    }

    fun getDefaultStartTime(): Pair<Int, Int> {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        return if (currentMinute < 30) {
            currentHour to 30
        } else {
            val nextHour =  (currentHour + 1) % 24
            nextHour to 0
        }
    }

    fun getDefaultEndTime(startHour: Int, startMinute: Int): Pair<Int, Int> {
        val endHour = (startHour + 1) % 24
        return endHour to startMinute
    }

}
