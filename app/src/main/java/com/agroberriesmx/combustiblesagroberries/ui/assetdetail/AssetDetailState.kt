package com.agroberriesmx.combustiblesagroberries.ui.assetdetail

sealed class AssetDetailState{
    data object Loading: AssetDetailState()

    data class Error(val error: String): AssetDetailState()
    data class Success(
        /*val cCodigoAfi: String,
        val cNumeconAfi: String,
        val vNombreAfi: String,
        val vNumserieAfi: String,
        val vObservacionAfi: String,
        val vPlacasAfi: String,
        val nKmAfi: String*/
        val numecon: String,
        val nombreAfi: String,
        val placas: String,
        val fecha: String,
        val litros: String,
        val campo: String,
        val zona: String,
        val actividad: String
    ): AssetDetailState()
}