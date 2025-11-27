package com.agroberriesmx.combustiblesagroberries.domain.usecase

import com.agroberriesmx.combustiblesagroberries.domain.Repository
import com.agroberriesmx.combustiblesagroberries.domain.model.FormattedRecordsModel
import javax.inject.Inject

class UploadUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(records: List<FormattedRecordsModel>): String {
        val response = repository.uploadRecords(records)
        return when (response.second) {
            200 -> "Ok"
            401 -> "Unauthorized"
            else -> response.first ?: "Error desconocido"
        }
    }
}