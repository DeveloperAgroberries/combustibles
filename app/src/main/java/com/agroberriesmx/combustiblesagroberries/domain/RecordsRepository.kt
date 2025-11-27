package com.agroberriesmx.combustiblesagroberries.domain

import com.agroberriesmx.combustiblesagroberries.domain.model.RecordModel


interface RecordsRepository {
    suspend fun getAllRecords(): List<RecordModel>
    suspend fun getRecordsByDate(): List<RecordModel>?
    suspend fun getRecordByControlLog(cControlCom: Long): RecordModel?
    suspend fun getUnsynchronizedRecords(): List<RecordModel>?
    suspend fun listUnsynchronizedRecords(): List<RecordModel>?
    suspend fun insertFuelRegister(record: RecordModel): Long
    suspend fun updateRecord(record: RecordModel): Int?

}