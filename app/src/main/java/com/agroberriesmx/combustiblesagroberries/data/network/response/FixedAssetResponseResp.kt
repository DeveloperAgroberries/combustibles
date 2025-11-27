package com.agroberriesmx.combustiblesagroberries.data.network.response

import com.agroberriesmx.combustiblesagroberries.domain.model.FixedAssetModelResp
import com.google.gson.annotations.SerializedName

data class FixedAssetApiResponseResp(
@SerializedName("response") val response: FixedAssetResponse
)
data class FixedAssetResponseResp(
    @SerializedName("cCodigoAfi") val cCodigoAfi: String,
    @SerializedName("cNumeconAfi") val cNumeconAfi: String,
    @SerializedName("vNombreAfi") val vNombreAfi: String,
    @SerializedName("vNumserieAfi") val vNumserieAfi: String,
    @SerializedName("vObservacionAfi") val vObservacionAfi: String,
    @SerializedName("vPlacasAfi") val vPlacasAfi: String,
    @SerializedName("nKmAfi") val nKmAfi: String
) {
    fun toDomain(): FixedAssetModelResp {
        return FixedAssetModelResp(
            cCodigoAfi = cCodigoAfi,
            cNumeconAfi = cNumeconAfi,
            vNombreAfi = vNombreAfi,
            vNumserieAfi = vNumserieAfi,
            vObservacionAfi = vObservacionAfi,
            vPlacasAfi = vPlacasAfi,
            nKmAfi = nKmAfi,
        )
    }
}
