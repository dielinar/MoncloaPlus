package com.example.moncloaplus.model.service

import android.net.Uri
import com.example.moncloaplus.model.Fix
import com.example.moncloaplus.model.FixState

interface FixService {
    suspend fun createFix(fix: Fix, imageUri: Uri?): Fix
    suspend fun getUserFixes(state: Int): List<Fix>
    suspend fun deleteFix(fix: Fix)
    suspend fun getAllFixesByState(state: Int): List<Fix>
    suspend fun updateFixState(fix: Fix, newState: FixState)
}
