package com.agroberriesmx.combustiblesagroberries.data.network.response

import com.agroberriesmx.combustiblesagroberries.domain.model.FieldModel
import com.google.gson.annotations.SerializedName

data class FieldsApiResponse(
    @SerializedName("response") val response: List<FieldsResponse>
)

data class FieldsResponse(
    @SerializedName("vNombreCam") val vNombreCam: String,
    @SerializedName("cCodigoCam") val cCodigoCam: String,
    @SerializedName("cCodigoZon") val cCodigoZon: String
) {
    fun toDomain(): FieldModel {
        return FieldModel(
            vNombreCam = vNombreCam,
            cCodigoCam = cCodigoCam,
            cCodigoZon = cCodigoZon
        )
    }
}