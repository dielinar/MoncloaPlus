package com.example.moncloaplus.screens.authentication

import com.example.moncloaplus.SIGN_IN_SCREEN
import com.example.moncloaplus.data.model.User
import com.example.moncloaplus.service.AccountService
import com.example.moncloaplus.service.StorageService
import com.example.moncloaplus.screens.PlusViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AccountCenterViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService
) : PlusViewModel() {

    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user.asStateFlow()

    init {
        launchCatching {
            _user.value = storageService.getUser(accountService.currentUserId)!!
        }
    }

    fun onSignOutClick(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.signOut()
            restartApp(SIGN_IN_SCREEN)
        }
    }

}
