package com.agroberriesmx.combustiblesagroberries.ui.fuel

import com.agroberriesmx.combustiblesagroberries.domain.model.FieldModel
import com.agroberriesmx.combustiblesagroberries.domain.model.FixedAssetModel
import com.agroberriesmx.combustiblesagroberries.domain.model.WorkerModel

sealed class FuelState {
    data object Loading: FuelState()

    data class Error(val error: String): FuelState()
    data class SuccessFixedAsset(val successFixedAsset: FixedAssetModel): FuelState()
    data class SuccessWorker(val successWorker: WorkerModel): FuelState()
    data class SuccessField(val successField: List<FieldModel>): FuelState()
}