package com.agroberriesmx.combustiblesagroberries.domain.usecase


import com.agroberriesmx.combustiblesagroberries.data.network.request.SyncRequest
import com.agroberriesmx.combustiblesagroberries.domain.Repository
import com.agroberriesmx.combustiblesagroberries.domain.model.LoginModel
import javax.inject.Inject

class LoginsUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(syncRequest: SyncRequest): List<LoginModel>? =
        repository.getLogins(syncRequest)
}