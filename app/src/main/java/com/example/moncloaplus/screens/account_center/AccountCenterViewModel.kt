package com.example.moncloaplus.screens.account_center

import com.example.moncloaplus.SIGN_IN_SCREEN
import com.example.moncloaplus.SPLASH_SCREEN
import com.example.moncloaplus.model.User
import com.example.moncloaplus.model.service.AccountService
import com.example.moncloaplus.model.service.StorageService
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
//            _user.value = accountService.getUserProfile()
        }
    }

    fun onUpdateDisplayNameClick(newDisplayName: String) {
        launchCatching {
            accountService.updateDisplayName(newDisplayName)
            _user.value = accountService.getUserProfile()
        }
    }

    fun onSignInClick(openScreen: (String) -> Unit) = openScreen(SIGN_IN_SCREEN)

    fun onSignOutClick(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.signOut()
            restartApp(SIGN_IN_SCREEN)
        }
    }

    fun onDeleteAccountClick(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.deleteAccount()
            restartApp(SPLASH_SCREEN)
        }
    }
}