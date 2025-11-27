package com.agroberriesmx.combustiblesagroberries.data.local

import com.agroberriesmx.combustiblesagroberries.domain.model.LoginModel
import com.agroberriesmx.combustiblesagroberries.domain.model.RecordModel

interface CombustiblesLocalDBService {
    suspend fun getAllRecords(): List<RecordModel>
    suspend fun getRecordsByDate(): List<RecordModel>?
    suspend fun getRecordByControlLog(cControlCom: Long): RecordModel?
    suspend fun getUnsynchronizedRecords(): List<RecordModel>?
    suspend fun listUnsynchronizedRecords(): List<RecordModel>?
    suspend fun insertFuelRegister(
        date: String,
        weekNumber: String,
        fixedAssetCode: String,
        fixedAssetName: String,
        odometer: String,
        workerCode: String,
        workerName: String,
        automatic: Int,
        combustible: String,
        combustibleName: String,
        liters: String,
        field: String,
        fieldName: String,
        activity: String,
        activityName: String,
        zoneCode: String,
        cCodigoUsu: String,
        isSynced: Int,
        nPrecioCom: String // <--- ¡AÑADIDO ESTE PARÁMETRO!
    ): Long
    suspend fun updateRecord(
        cControlCom: Long,
        date: String,
        weekNumber: String,
        fixedAssetCode: String,
        fixedAssetName: String,
        odometer: String,
        workerCode: String,
        workerName: String,
        automatic: Int,
        combustible: String,
        combustibleName: String,
        liters: String,
        field: String,
        fieldName: String,
        activity: String,
        activityName: String,
        zoneCode: String,
        cCodigoUsu: String,
        isSynced: Int,
        nPrecioCom: String // <--- ¡AÑADIDO ESTE PARÁMETRO!
    ): Int?

    //Credentials
    suspend fun getUserByCodeAndPassword(cUsu: String, vPassword: String): LoginModel?
    suspend fun getAllUsers(): List<LoginModel>
    suspend fun insertUsers(users: List<LoginModel>): List<Long?>
    suspend fun deleteAllUsers()
}