package com.example.moncloaplus.model.service

import com.example.moncloaplus.model.User

interface StorageService {
    suspend fun addUser(user: User)
    suspend fun getUser(userId: String): User?
    suspend fun updateUser(user: User)
    suspend fun deleteUser(userId: String)
    suspend fun getUsers(): List<User>
}