package com.agroberriesmx.combustiblesagroberries.data

import com.agroberriesmx.combustiblesagroberries.data.local.CombustiblesLocalDBService
import com.agroberriesmx.combustiblesagroberries.domain.RecordsRepository
import com.agroberriesmx.combustiblesagroberries.domain.model.RecordModel
import javax.inject.Inject

class RecordsRepositoryImpl @Inject constructor(
    private val localDBService: CombustiblesLocalDBService
) : RecordsRepository {
    override suspend fun getAllRecords(): List<RecordModel> {
        return localDBService.getAllRecords()
    }

    override suspend fun getRecordsByDate(): List<RecordModel>? {
        return localDBService.getRecordsByDate()
    }

    override suspend fun getRecordByControlLog(cControlCom: Long): RecordModel? {
        return localDBService.getRecordByControlLog(cControlCom)
    }

    override suspend fun getUnsynchronizedRecords(): List<RecordModel>? {
        return localDBService.getUnsynchronizedRecords()
    }

    override suspend fun listUnsynchronizedRecords(): List<RecordModel>? {
        return localDBService.listUnsynchronizedRecords()
    }

    override suspend fun insertFuelRegister(record: RecordModel): Long {
        return localDBService.insertFuelRegister(
            record.date,
            record.weekNumber,
            record.fixedAssetCode,
            record.fixedAssetName,
            record.odometer,
            record.workerCode,
            record.workerName,
            record.automatic,
            record.combustible,
            record.combustibleName,
            record.liters,
            record.field,
            record.fieldName,
            record.activity,
            record.activityName,
            record.zoneCode,
            record.cCodigoUsu,
            record.isSynced,
            record.nPrecioCom
        )
    }

    override suspend fun updateRecord(record: RecordModel): Int? {
        return localDBService.updateRecord(
            record.cControlCom,
            record.date,
            record.weekNumber,
            record.fixedAssetCode,
            record.fixedAssetName,
            record.odometer,
            record.workerCode,
            record.workerName,
            record.automatic,
            record.combustible,
            record.combustibleName,
            record.liters,
            record.field,
            record.fieldName,
            record.activity,
            record.activityName,
            record.zoneCode,
            record.cCodigoUsu,
            record.isSynced,
            record.nPrecioCom
        )
    }
}