package com.agroberriesmx.combustiblesagroberries.domain.model

/*data class FixedAssetModel(
    val cCodigoAfi: String,
    val cNumeconAfi: String,
    val vNombreAfi: String,
    val vNumserieAfi: String,
    val vObservacionAfi: String,
    val vPlacasAfi: String,
    val nKmAfi: String
)*/
data class FixedAssetModel(
    val numecon: String,
    val nombreAfi: String,
    val placas: String,
    val fecha: String,
    val litros: String,
    val campo: String,
    val zona: String,
    val actividad: String
)