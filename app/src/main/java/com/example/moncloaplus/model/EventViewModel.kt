package com.example.moncloaplus.model

import android.net.Uri
import com.example.moncloaplus.model.service.EventService
import com.example.moncloaplus.screens.PlusViewModel
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

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri.asStateFlow()

    private val _date = MutableStateFlow(System.currentTimeMillis())
    val date = _date.asStateFlow()

    fun updateTitle(newTitle: String) { _title.value = newTitle }
    fun updateDescription(newDescription: String) { _description.value = newDescription }
    fun updateDate(newDate: Long) { _date.value = newDate }

}
