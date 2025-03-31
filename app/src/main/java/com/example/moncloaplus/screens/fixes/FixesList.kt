package com.example.moncloaplus.screens.fixes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.example.moncloaplus.model.Fix
import com.example.moncloaplus.model.FixViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FixesList(
    viewModel: FixViewModel,
    fixesList: List<Fix>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(fixesList) { fix ->
            FixCard(viewModel, fix)
        }
    }
}

@Composable
fun FixCard(
    viewModel: FixViewModel,
    fix: Fix
) {

}
