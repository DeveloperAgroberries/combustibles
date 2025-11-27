package com.agroberriesmx.combustiblesagroberries.domain

import com.agroberriesmx.combustiblesagroberries.domain.model.LoginModel

interface CredentialsRepository {
    suspend fun getUserByCodeAndPassword(cUsu: String, vPassword: String): LoginModel?
    suspend fun insertUsers(users: List<LoginModel>): List<Long?>
    suspend fun deleteAllUsers()
}