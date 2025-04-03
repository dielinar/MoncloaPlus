package com.example.moncloaplus.model.service

import android.net.Uri
import com.example.moncloaplus.model.Fix

interface FixService {
    suspend fun createFix(fix: Fix, imageUri: Uri?): Fix
    suspend fun getFix(fixId: String): Fix?
    suspend fun getUserFixes(state: Int): List<Fix>
    suspend fun deleteFix(fix: Fix)
}
