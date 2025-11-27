package com.agroberriesmx.combustiblesagroberries.domain.usecase

import com.agroberriesmx.combustiblesagroberries.data.network.request.LoginRequest
import com.agroberriesmx.combustiblesagroberries.domain.Repository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(
        loginRequest: LoginRequest
    ) = repository.getToken(loginRequest)
}