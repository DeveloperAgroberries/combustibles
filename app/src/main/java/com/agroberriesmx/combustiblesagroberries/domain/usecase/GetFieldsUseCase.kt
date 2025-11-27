package com.agroberriesmx.combustiblesagroberries.domain.usecase

import com.agroberriesmx.combustiblesagroberries.domain.Repository
import javax.inject.Inject

class GetFieldsUseCase @Inject constructor(private val repository: Repository){
    suspend operator fun invoke() = repository.getFields()
}