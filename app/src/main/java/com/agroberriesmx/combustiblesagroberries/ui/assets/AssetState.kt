package com.agroberriesmx.combustiblesagroberries.ui.assets

import com.agroberriesmx.combustiblesagroberries.domain.model.FixedAssetModel
import com.agroberriesmx.combustiblesagroberries.ui.fuel.FuelState

sealed class AssetState {
    data object Loading: AssetState()

    data class Error(val error: String): AssetState()
    data class SuccessAsset(val successAsset: List<FixedAssetModel>): AssetState()
}