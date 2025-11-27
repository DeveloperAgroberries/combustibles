package com.agroberriesmx.combustiblesagroberries.data.network.response

import com.agroberriesmx.combustiblesagroberries.domain.model.FixedAssetModel
import com.google.gson.annotations.SerializedName

data class FixedAssetApiResponse(
    @SerializedName("response") val response: FixedAssetResponse
)

/*data class FixedAssetResponse(
    @SerializedName("cCodigoAfi") val cCodigoAfi: String,
    @SerializedName("cNumeconAfi") val cNumeconAfi: String,
    @SerializedName("vNombreAfi") val vNombreAfi: String,
    @SerializedName("vNumserieAfi") val vNumserieAfi: String,
    @SerializedName("vObservacionAfi") val vObservacionAfi: String,
    @SerializedName("vPlacasAfi") val vPlacasAfi: String,
    @SerializedName("nKmAfi") val nKmAfi: String
) {
    fun toDomain(): FixedAssetModel {
        return FixedAssetModel(
            cCodigoAfi = cCodigoAfi,
            cNumeconAfi = cNumeconAfi,
            vNombreAfi = vNombreAfi,
            vNumserieAfi = vNumserieAfi,
            vObservacionAfi = vObservacionAfi,
            vPlacasAfi = vPlacasAfi,
            nKmAfi = nKmAfi
        )
    }
}*/
data class FixedAssetResponse(
    @SerializedName("numecon") val numecon: String,
    @SerializedName("nombreAfi") val nombreAfi: String,
    @SerializedName("placas") val placas: String,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("litros") val litros: String,
    @SerializedName("campo") val campo: String,
    @SerializedName("zona") val zona: String,
    @SerializedName("actividad") val actividad: String
) {
    fun toDomain(): FixedAssetModel {
        return FixedAssetModel(
            numecon = numecon,
            nombreAfi = nombreAfi,
            placas = placas,
            fecha = fecha,
            litros = litros,
            campo = campo,
            zona = zona,
            actividad = actividad
        )
    }
}