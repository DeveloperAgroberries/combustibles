package com.agroberriesmx.combustiblesagroberries.ui.recordsdetail

sealed class ListRecordDetailState {
    data object Loading: ListRecordDetailState()

    data class Error(val error: String): ListRecordDetailState()
    data class Success(
        val cControlCom:Long,
        val date: String,
        val weekNumber: String,
        val fixedAssetCode: String,
        val fixedAssetName: String,
        val odometer: String,
        val workerCode: String,
        val workerName: String,
        val automatic: Int,
        val combustible: String,
        val combustibleName: String,
        val liters: String,
        val precioCom: String,
        val field: String,
        val fieldName: String,
        val activity: String,
        val activityName: String,
        val cCodigoUsu: String,
        val isSynced: Int
    ): ListRecordDetailState()
}