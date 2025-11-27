package com.agroberriesmx.combustiblesagroberries.domain.usecase

import com.agroberriesmx.combustiblesagroberries.domain.Repository
import javax.inject.Inject

class GetFixedAssetUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(asset: String) = repository.getFixedAssetData(asset)
}