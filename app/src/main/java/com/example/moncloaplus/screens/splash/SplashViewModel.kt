package com.example.moncloaplus.screens.splash

import com.example.moncloaplus.HOME_SCREEN
import com.example.moncloaplus.SPLASH_SCREEN
import com.example.moncloaplus.model.service.AccountService
import com.example.moncloaplus.screens.PlusViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountService: AccountService
) : PlusViewModel() {

    fun onAppStart(openAndPopUp: (String, String) -> Unit) {
        if (accountService.hasUser()) openAndPopUp(HOME_SCREEN, SPLASH_SCREEN)
        else createAnonymousAccount(openAndPopUp)
    }

    private fun createAnonymousAccount(openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            accountService.createAnonymousAccount()
            openAndPopUp(HOME_SCREEN, SPLASH_SCREEN)
        }
    }
}