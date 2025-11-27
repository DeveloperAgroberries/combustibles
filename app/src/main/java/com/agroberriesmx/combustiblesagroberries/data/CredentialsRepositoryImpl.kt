package com.agroberriesmx.combustiblesagroberries.data

import com.agroberriesmx.combustiblesagroberries.data.local.CombustiblesLocalDBService
import com.agroberriesmx.combustiblesagroberries.domain.CredentialsRepository
import com.agroberriesmx.combustiblesagroberries.domain.model.LoginModel
import javax.inject.Inject

class CredentialsRepositoryImpl @Inject constructor(
    private val localDBService: CombustiblesLocalDBService
) : CredentialsRepository {
    override suspend fun getUserByCodeAndPassword(cUsu: String, vPassword: String): LoginModel? {
        return localDBService.getUserByCodeAndPassword(cUsu, vPassword)
    }

    override suspend fun insertUsers(users: List<LoginModel>): List<Long?> {
        return localDBService.insertUsers(users)
    }

    override suspend fun deleteAllUsers() {}

}