package com.agroberriesmx.combustiblesagroberries.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecordModel (
    val cControlCom: Long,
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
    val field: String,
    val fieldName: String,
    val activity: String,
    val activityName: String,
    val zoneCode: String,
    val cCodigoUsu: String,
    var isSynced: Int,
    val nPrecioCom: String // ¡NUEVO CAMPO AQUÍ! Asegúrate de que el tipo de dato coincida con lo que esperas.
): Parcelable