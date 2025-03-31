package com.example.moncloaplus.model.service

import android.net.Uri
import com.example.moncloaplus.model.Fix

interface FixService {
    suspend fun addFix(fix: Fix, imageUri: Uri?): Fix
}
