package com.agroberriesmx.combustiblesagroberries.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FormattedRecordsModel(
    val dConsumoCom: String,
    val cSemanaCom: String,
    val cNumeconAfi: String,
    val nKmCom: Int,
    val cCodigoTra: String,
    val vNombreTra: String,
    val cManualCom: String,
    val cTipoCom: String,
    val nLitrosCom: Float,
    val cCodigoCam: String,
    val cCodigoAct: String,
    val cCodigoZon: String,
    val cCodigoUsu: String,
    val dCreacionCom: String,
    val nPrecioCom: Float
) : Parcelable