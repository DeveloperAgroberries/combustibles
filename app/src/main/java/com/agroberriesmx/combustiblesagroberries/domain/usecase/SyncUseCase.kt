package com.agroberriesmx.combustiblesagroberries.domain.usecase

import com.agroberriesmx.combustiblesagroberries.data.network.request.SyncRequest
import com.agroberriesmx.combustiblesagroberries.domain.Repository
import javax.inject.Inject

class SyncUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(
        syncRequest: SyncRequest
    ) = repository.getLogins(syncRequest)
}