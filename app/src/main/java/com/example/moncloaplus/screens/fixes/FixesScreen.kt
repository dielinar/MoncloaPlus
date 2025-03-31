package com.example.moncloaplus.screens.fixes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moncloaplus.model.FixViewModel
import com.example.moncloaplus.screens.account_center.AccountCenterViewModel

@Composable
fun FixesScreen(
    fixViewModel: FixViewModel = hiltViewModel(),
    accViewModel: AccountCenterViewModel = hiltViewModel()
) {
    val userFixes by fixViewModel.userFixes.collectAsState()
    val currentUser by accViewModel.user.collectAsState()
    val isLoading by fixViewModel.isLoading.collectAsState()

    Scaffold(
        floatingActionButton = {
            NewFixButton(currentUser, fixViewModel)
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                FixesSegmentedButton (
                    FIXES_STATES_OPTIONS,
                    onOptionClick = {}
                )
            }
        }
    }

}
