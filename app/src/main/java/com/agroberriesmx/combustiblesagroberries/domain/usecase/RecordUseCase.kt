package com.agroberriesmx.combustiblesagroberries.domain.usecase

import com.agroberriesmx.combustiblesagroberries.domain.RecordsRepository
import javax.inject.Inject

class RecordUseCase @Inject constructor(private val recordsRepository: RecordsRepository) {
    suspend operator fun invoke(cControlCom: Long) = recordsRepository.getRecordByControlLog(cControlCom)
}