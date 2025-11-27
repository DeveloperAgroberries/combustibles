package com.agroberriesmx.combustiblesagroberries.domain.usecase

import com.agroberriesmx.combustiblesagroberries.domain.Repository
import javax.inject.Inject

class GetWorkerUseCase @Inject constructor(private val repository: Repository){
    suspend operator fun invoke(worker: String) = repository.getWorkerData(worker)
}