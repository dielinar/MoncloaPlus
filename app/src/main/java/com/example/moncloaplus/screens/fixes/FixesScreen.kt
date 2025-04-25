package com.example.moncloaplus.screens.fixes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moncloaplus.model.FixState
import com.example.moncloaplus.model.FixViewModel
import com.example.moncloaplus.screens.account_center.AccountCenterViewModel
import com.example.moncloaplus.screens.reservation.LoadingIndicator

@Composable
fun FixesScreen(
    fixViewModel: FixViewModel = hiltViewModel(),
    accViewModel: AccountCenterViewModel = hiltViewModel()
) {
    val allFixes by fixViewModel.allFixes.collectAsState()
    val userFixes by fixViewModel.userFixes.collectAsState()
    val currentUser by accViewModel.user.collectAsState()
    val isLoading by fixViewModel.isLoading.collectAsState()

    var selectedState by remember { mutableIntStateOf(FixState.PENDING.ordinal) }

    LaunchedEffect(Unit) {
        fixViewModel.fetchUserFixes(selectedState)
        fixViewModel.fetchAllFixes(selectedState)
    }

    Scaffold(
        floatingActionButton =  {
            if (currentUser.canCreateFixes()) {
                NewFixButton(currentUser, fixViewModel)
            }
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
                    onOptionClick = {
                        selectedState = it
                        fixViewModel.fetchUserFixes(it)
                        fixViewModel.fetchAllFixes(it)
                    }
                )
            }

            val fixesList = if (currentUser.isMaintainer())
                allFixes[selectedState]
            else
                userFixes[selectedState]


            if (isLoading || fixesList == null) {
                LoadingIndicator()
            } else if (fixesList.isEmpty()) {
                Text(
                    text = getEmptyFixesMessage(selectedState),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic
                )
            } else {
                FixesList(fixViewModel, fixesList, currentUser)
            }
        }
    }

}

@Composable
fun getEmptyFixesMessage(state: Int): String {
    return when (state) {
        FixState.PENDING.ordinal -> "No hay arreglos pendientes."
        FixState.IN_PROGRESS.ordinal -> "No hay arreglos en curso."
        FixState.FIXED.ordinal -> "No hay arreglos completados."
        else -> "No hay arreglos disponibles."
    }
}
