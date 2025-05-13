package com.example.moncloaplus.data.model

data class UploadResult(
    val fileName: String,
    val downloadUrl: String,
    val path: String,
    val size: Long
)
