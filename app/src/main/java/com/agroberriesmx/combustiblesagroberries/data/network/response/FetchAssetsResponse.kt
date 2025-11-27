package com.agroberriesmx.combustiblesagroberries.data.network.response

import com.google.gson.annotations.SerializedName

data class FetchAssetsResponse (
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("response") val data: List<FixedAssetResponse>?
)