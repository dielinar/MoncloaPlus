package com.example.moncloaplus.model

import com.example.moncloaplus.ADMIN_SCREEN
import com.example.moncloaplus.MAIN_SCREEN
import com.example.moncloaplus.SIGN_IN_SCREEN
import com.example.moncloaplus.SnackbarManager
import com.example.moncloaplus.USER_DATA_SCREEN
import com.example.moncloaplus.model.service.AccountService
import com.example.moncloaplus.model.service.StorageService
import com.example.moncloaplus.screens.PlusViewModel
import com.example.moncloaplus.screens.user_data.areValidInitials
import com.example.moncloaplus.screens.user_data.isValidRoom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor (
    private val storageService: StorageService,
    private val accountService: AccountService
): PlusViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> = _firstName.asStateFlow()

    private val _firstSurname = MutableStateFlow("")
    val firstSurname: StateFlow<String> = _firstSurname.asStateFlow()

    private val _secondSurname = MutableStateFlow("")
    val secondSurname: StateFlow<String> = _secondSurname.asStateFlow()

    private val _room = MutableStateFlow("")
    val room: StateFlow<String> = _room.asStateFlow()
    private val _isRoomValid = MutableStateFlow(true)
    val isRoomValid: StateFlow<Boolean> = _isRoomValid.asStateFlow()

    private val _initials = MutableStateFlow("")
    val initials: StateFlow<String> = _initials.asStateFlow()
    private val _areInitialsValid = MutableStateFlow(true)
    val areInitialsValid: StateFlow<Boolean> = _areInitialsValid.asStateFlow()

    private val _city = MutableStateFlow("")
    val city: StateFlow<String> = _city.asStateFlow()

    private val _degree = MutableStateFlow("")
    val degree: StateFlow<String> = _degree.asStateFlow()

    private val _university = MutableStateFlow("")
    val university: StateFlow<String> = _university.asStateFlow()

    val isFormValid = combine(
        firstName, firstSurname, secondSurname, room, initials, city, degree, university
    ) { fields ->
        fields.all { it.isNotBlank() }
    }

    fun updateFirstName(newFirstName: String) { _firstName.value = newFirstName }
    fun updateFirstSurname(newFirstSurname: String) { _firstSurname.value = newFirstSurname }
    fun updateSecondSurname(newSecondSurname: String) { _secondSurname.value = newSecondSurname }
    fun updateRoom(newRoom: String) {
        _room.value = newRoom
        _isRoomValid.value = newRoom.isValidRoom()
    }
    fun updateInitials(newInitials: String) {
        _initials.value = newInitials
        _areInitialsValid.value = newInitials.areValidInitials()
    }
    fun updateCity(newCity: String) { _city.value = newCity }
    fun updateDegree(newDegree: String) { _degree.value = newDegree }
    fun updateUniversity(newUniversity: String) { _university.value = newUniversity }

    fun saveUserData(openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            try {
                val currentUser = accountService.getUserProfile()

                val user = User(
                    id = currentUser.id,
                    email = currentUser.email,
                    provider = currentUser.provider,
                    firstName = firstName.value,
                    firstSurname = firstSurname.value,
                    secondSurname = secondSurname.value,
                    displayName = firstName.value,
                    initials = initials.value,
                    roomNumber = room.value,
                    city = city.value,
                    degree = degree.value,
                    university = university.value,
                    role = UserRole.NORMAL
                )

                storageService.addUser(user)

                if (user.isAdmin()) {
                    openAndPopUp(ADMIN_SCREEN, SIGN_IN_SCREEN)
                }
                else {
                    openAndPopUp(MAIN_SCREEN, USER_DATA_SCREEN)
                }
            }
            catch (e: Exception) {
                SnackbarManager.showMessage("Error desconocido al guardar los datos.")
            }
        }
    }

    fun addUser(user: User) {
        launchCatching {
            storageService.addUser(user)
        }
    }

    fun getUser(userId: String, onResult: (User?) -> Unit) {
        launchCatching {
            val user = storageService.getUser(userId)
            onResult(user)
        }
    }

    fun fetchUsers() {
        launchCatching {
            val userList = storageService.getUsers()
            _users.value = userList
        }
    }

    fun updateUser(user: User) {
        launchCatching {
            storageService.updateUser(user)
        }
    }

    fun deleteUser(userId: String) {
        launchCatching {
            storageService.deleteUser(userId)
        }
    }

}