package com.agroberriesmx.combustiblesagroberries.data.local

import android.os.Build
import androidx.annotation.RequiresApi
import com.agroberriesmx.combustiblesagroberries.domain.model.LoginModel
import com.agroberriesmx.combustiblesagroberries.domain.model.RecordModel
import javax.inject.Inject

class CombustiblesLocalDBServiceImpl @Inject constructor(private val databaseHelper: DatabaseHelper): CombustiblesLocalDBService {
    override suspend fun getAllRecords(): List<RecordModel> {
        return databaseHelper.getAllRecords()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getRecordsByDate(): List<RecordModel>? {
        return databaseHelper.getRecordsByDate()
    }

    override suspend fun getRecordByControlLog(cControlCom: Long): RecordModel? {
        return databaseHelper.getRecordByControlLog(cControlCom)
    }

    override suspend fun getUnsynchronizedRecords(): List<RecordModel>? {
        return databaseHelper.getUnsynchronizedRecords()
    }

    override suspend fun listUnsynchronizedRecords(): List<RecordModel>? {
        return databaseHelper.listUnsynchronizedRecords()
    }

    override suspend fun insertFuelRegister(
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
        nPrecioCom: String
    ): Long {
        return databaseHelper.insertFuelRegister(
            date = date,
            weekNumber = weekNumber,
            fixedAssetCode = fixedAssetCode,
            fixedAssetName = fixedAssetName,
            odometer = odometer,
            workerCode = workerCode,
            workerName = workerName,
            automatic = automatic,
            combustible = combustible,
            combustibleName = combustibleName,
            liters = liters,
            field = field,
            fieldName = fieldName,
            activity = activity,
            activityName = activityName,
            zoneCode = zoneCode,
            cCodigoUsu = cCodigoUsu,
            isSynced = isSynced,
            nPrecioCom = nPrecioCom
        )
    }

    override suspend fun updateRecord(
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
        nPrecioCom: String
    ): Int? {
        return databaseHelper.updateRecord(
            cControlCom = cControlCom,
            date = date,
            weekNumber = weekNumber,
            fixedAssetCode = fixedAssetCode,
            fixedAssetName = fixedAssetName,
            odometer = odometer,
            workerCode = workerCode,
            workerName = workerName,
            automatic = automatic,
            combustible = combustible,
            combustibleName = combustibleName,
            liters = liters,
            field = field,
            fieldName = fieldName,
            activity = activity,
            activityName = activityName,
            zoneCode = zoneCode,
            cCodigoUsu = cCodigoUsu,
            isSynced = isSynced,
            nPrecioCom = nPrecioCom
        )
    }

    //Credentials
    override suspend fun getUserByCodeAndPassword(cUsu: String, vPassword: String): LoginModel?{
        return databaseHelper.getUserByCodeAndPassword(cUsu, vPassword)
    }

    override suspend fun getAllUsers(): List<LoginModel>{
        return databaseHelper.getAllUsers()
    }

    override suspend fun insertUsers(users: List<LoginModel>): List<Long?> {
        return users.map{
                user ->
            databaseHelper.insertUser(
                vNombreUsu = user.vNombreUsu,
                cCodigoUsu = user.cCodigoUsu,
                vPasswordUsu = user.vPasswordUsu
            )
        }
    }

    override suspend fun deleteAllUsers() {
        return databaseHelper.deleteAllUsers()
    }
}