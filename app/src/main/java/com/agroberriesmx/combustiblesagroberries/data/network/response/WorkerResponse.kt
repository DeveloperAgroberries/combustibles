package com.agroberriesmx.combustiblesagroberries.data.network.response

import com.agroberriesmx.combustiblesagroberries.domain.model.WorkerModel
import com.google.gson.annotations.SerializedName

data class WorkerApiResponse(
    @SerializedName("response") val response: WorkerResponse
)

data class WorkerResponse(
    @SerializedName("cCodigoUsu") val cCodigoUsu: String,
    @SerializedName("vNombreUsu") val vNombreUsu: String,
    //@SerializedName("vNombreTra") val vNombreTra: String,
    //@SerializedName("vApellidopatTra") val vApellidopatTra: String,
    //@SerializedName("vApellidomatTra") val vApellidomatTra: String
) {
    fun toDomain(): WorkerModel{
        return WorkerModel(
            cCodigoUsu = cCodigoUsu,
            vNombreUsu = vNombreUsu
            //vNombreTra = vNombreTra,
            //vApellidopatTra = vApellidopatTra,
            //vApellidomatTra = vApellidomatTra
        )
    }
}