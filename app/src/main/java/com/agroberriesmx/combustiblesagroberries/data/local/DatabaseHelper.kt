package com.agroberriesmx.combustiblesagroberries.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.agroberriesmx.combustiblesagroberries.domain.model.LoginModel
import com.agroberriesmx.combustiblesagroberries.domain.model.RecordModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "combustibles.db"
        private const val DATABASE_VERSION = 4 // si sealizan updates a la DB hay subir el numero de version: RICARDO DIMAS 29/07/2025

        private const val CREATE_TABLE_LOGINS = """
            CREATE TABLE genlogin (
                controlLog INTEGER PRIMARY KEY AUTOINCREMENT,
                vNombreUsu TEXT,
                cCodigoUsu TEXT,
                vPasswordUsu TEXT
            )
        """

        private const val CREATE_TABLE_FUEL_RECORDS = """
            CREATE TABLE z_gencontrolcomb (
                cControlCom INTEGER PRIMARY KEY AUTOINCREMENT,
                dConsumoCom TEXT,
                cSemanaCom TEXT,
                cCodigoAfi TEXT,
                vNombreAfi TEXT,
                nKmCom TEXT,
                cCodigoTra TEXT,
                vNombreTra TEXT,
                cManualCom TEXT,
                cTipoCom TEXT,
                vNombreCom TEXT,
                nLitrosCom TEXT,
                cCodigoCam TEXT,
                vNombreCam TEXT,
                cCodigoAct TEXT,
                vNombreAct TEXT,
                cCodigoZon TEXT,
                cCodigoUsu TEXT,
                isSynced INTEGER DEFAULT 0,
                nPrecioCom TEXT NOT NULL DEFAULT '' -- ¡NUEVA LÍNEA AQUÍ!
            )
        """

        private const val CREATE_TABLE_FIELDS = """
            CREATE TABLE gencampo (
                cCodigoTem TEXT,
                vNombreCam TEXT,
                cCodigoCam TEXT,
                cCodigoZon TEXT
            )
        """

        private const val CREATE_TABLE_FIXED_ASSET = """
             CREATE TABLE afiactivo (
                cCodigoAfi TEXT,
                cNumeconAfi TEXT,
                vNombreAfi TEXT,
                vNumserieAfi TEXT,
                vObservacionAfi TEXT,
                vPlacasAfi TEXT
             )
        """

        private const val CREATE_TABLE_WORKERS = """
            CREATE TABLE nomtrabajador (
                cCodigoTra TEXT,
                vNombreTra TEXT,
                vApellidopatTra TEXT,
                vApellidomatTra TEXT
             )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_LOGINS)
        db.execSQL(CREATE_TABLE_FUEL_RECORDS)
        db.execSQL(CREATE_TABLE_FIELDS)
        db.execSQL(CREATE_TABLE_FIXED_ASSET)
        db.execSQL(CREATE_TABLE_WORKERS)
    }

    private fun dropTables(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS genlogin")
        db.execSQL("DROP TABLE IF EXISTS z_gencontrolcomb")
        db.execSQL("DROP TABLE IF EXISTS gencampo")
        db.execSQL("DROP TABLE IF EXISTS afiactivo")
        db.execSQL("DROP TABLE IF EXISTS nomtrabajador")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        dropTables(db)
        onCreate(db)
        /*if (oldVersion < 2) {
            // Esta es la migración específica para pasar de la versión 1 a la versión 2
            // (cuando añadiste nPrecioCom)
            val ADD_NPRECIOCOM_COLUMN = "ALTER TABLE z_gencontrolcomb ADD COLUMN nPrecioCom TEXT NOT NULL DEFAULT '';"
            db.execSQL(ADD_NPRECIOCOM_COLUMN)
            Log.d("DatabaseHelper", "Added nPrecioCom column to z_gencontrolcomb table.")
        }*/
        // Si en el futuro haces más cambios de esquema, añadirías más bloques 'if'
        // if (oldVersion < 3) {
        //    // Migración de la versión 2 a la 3
        // }
        // Y así sucesivamente para cada nueva versión de la base de datos.
    }

    fun insertFuelRegister(
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
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("dConsumoCom", date)
            put("cSemanaCom", weekNumber)
            put("cCodigoAfi", fixedAssetCode)
            put("vNombreAfi", fixedAssetName)
            put("nKmCom", odometer)
            put("cCodigoTra", workerCode)
            put("vNombreTra", workerName)
            put("cManualCom", automatic)
            put("cTipoCom", combustible)
            put("vNombreCom", combustibleName)
            put("nLitrosCom", liters)
            put("cCodigoCam", field)
            put("vNombreCam", fieldName)
            put("cCodigoAct", activity)
            put("vNombreAct", activityName)
            put("cCodigoZon",zoneCode)
            put("cCodigoUsu", cCodigoUsu)
            put("isSynced", isSynced)
            put("nPrecioCom", nPrecioCom)
        }

        return db.insert("z_gencontrolcomb", null, values).also {
            db.close()
        }
    }

    fun getAllRecords(): List<RecordModel> {
        val db = this.writableDatabase
        val cursor = db.query(
            "z_gencontrolcomb",
            null,
            null,
            null,
            null,
            null,
            null,
        )

        val records = mutableListOf<RecordModel>()
        if (cursor.moveToFirst()) {
            do {
                val record = RecordModel(
                    cControlCom = cursor.getLong(cursor.getColumnIndexOrThrow("cControlCom")),
                    date = cursor.getString(cursor.getColumnIndexOrThrow("dConsumoCom")),
                    weekNumber = cursor.getString(cursor.getColumnIndexOrThrow("cSemanaCom")),
                    fixedAssetCode = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoAfi")),
                    fixedAssetName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreAfi")),
                    odometer = cursor.getString(cursor.getColumnIndexOrThrow("nKmCom")),
                    workerCode = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoTra")),
                    workerName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreTra")),
                    automatic = cursor.getInt(cursor.getColumnIndexOrThrow("cManualCom")),
                    combustible = cursor.getString(cursor.getColumnIndexOrThrow("cTipoCom")),
                    combustibleName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreCom")),
                    liters = cursor.getString(cursor.getColumnIndexOrThrow("nLitrosCom")),
                    field = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoCam")),
                    fieldName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreCam")),
                    activity = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoAct")),
                    activityName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreAct")),
                    zoneCode = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoZon")),
                    cCodigoUsu = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoUsu")),
                    isSynced = cursor.getInt(cursor.getColumnIndexOrThrow("isSynced")),
                    nPrecioCom = cursor.getString(cursor.getColumnIndexOrThrow("nPrecioCom"))
                )
                records.add(record)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return records
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getRecordsByDate(): List<RecordModel>? {
        val db = this.writableDatabase
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(formatter)

        val cursor = db.query(
            "z_gencontrolcomb",
            null,
            "DATE(dConsumoCom) = ?",
            arrayOf(formattedDate),
            null,
            null,
            null,
            null,
        )

        val records = mutableListOf<RecordModel>()

        try {
            if (cursor.moveToFirst()) {
                do {
                    val record = RecordModel(
                        cControlCom = cursor.getLong(cursor.getColumnIndexOrThrow("cControlCom")),
                        date = cursor.getString(cursor.getColumnIndexOrThrow("dConsumoCom")),
                        weekNumber = cursor.getString(cursor.getColumnIndexOrThrow("cSemanaCom")),
                        fixedAssetCode = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoAfi")),
                        fixedAssetName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreAfi")),
                        odometer = cursor.getString(cursor.getColumnIndexOrThrow("nKmCom")),
                        workerCode = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoTra")),
                        workerName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreTra")),
                        automatic = cursor.getInt(cursor.getColumnIndexOrThrow("cManualCom")),
                        combustible = cursor.getString(cursor.getColumnIndexOrThrow("cTipoCom")),
                        combustibleName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreCom")),
                        liters = cursor.getString(cursor.getColumnIndexOrThrow("nLitrosCom")),
                        field = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoCam")),
                        fieldName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreCam")),
                        activity = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoAct")),
                        activityName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreAct")),
                        zoneCode = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoZon")),
                        cCodigoUsu = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoUsu")),
                        isSynced = cursor.getInt(cursor.getColumnIndexOrThrow("isSynced")),
                        nPrecioCom = cursor.getString(cursor.getColumnIndexOrThrow("nPrecioCom"))
                    )
                    records.add(record)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
        }
        return if (records.isNotEmpty()) records else null
    }

    fun getRecordByControlLog(cControlCom: Long): RecordModel? {
        val db = this.writableDatabase
        val cursor = db.query(
            "z_gencontrolcomb",
            null,
            "cControlCom = ?",
            arrayOf(cControlCom.toString()),
            null,
            null,
            null,
            "1"
        )

        var record: RecordModel? = null
        if (cursor.moveToFirst()) {
            record = RecordModel(
                cControlCom = cursor.getLong(cursor.getColumnIndexOrThrow("cControlCom")),
                date = cursor.getString(cursor.getColumnIndexOrThrow("dConsumoCom")),
                weekNumber = cursor.getString(cursor.getColumnIndexOrThrow("cSemanaCom")),
                fixedAssetCode = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoAfi")),
                fixedAssetName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreAfi")),
                odometer = cursor.getString(cursor.getColumnIndexOrThrow("nKmCom")),
                workerCode = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoTra")),
                workerName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreTra")),
                automatic = cursor.getInt(cursor.getColumnIndexOrThrow("cManualCom")),
                combustible = cursor.getString(cursor.getColumnIndexOrThrow("cTipoCom")),
                combustibleName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreCom")),
                liters = cursor.getString(cursor.getColumnIndexOrThrow("nLitrosCom")),
                field = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoCam")),
                fieldName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreCam")),
                activity = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoAct")),
                activityName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreAct")),
                zoneCode = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoZon")),
                cCodigoUsu = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoUsu")),
                isSynced = cursor.getInt(cursor.getColumnIndexOrThrow("isSynced")),
                nPrecioCom = cursor.getString(cursor.getColumnIndexOrThrow("nPrecioCom"))
            )
        }

        cursor.close()
        return record
    }

    fun listUnsynchronizedRecords(): List<RecordModel>? {
        val db = this.writableDatabase

        val cursor = db.query(
            "z_gencontrolcomb",
            null,
            "isSynced = 0",
            null,
            null,
            null,
            null,
            null
        )

        val records = mutableListOf<RecordModel>()

        try {
            if (cursor.moveToFirst()) {
                do {
                    val record = RecordModel(
                        cControlCom = cursor.getLong(cursor.getColumnIndexOrThrow("cControlCom")),
                        date = cursor.getString(cursor.getColumnIndexOrThrow("dConsumoCom")),
                        weekNumber = cursor.getString(cursor.getColumnIndexOrThrow("cSemanaCom")),
                        fixedAssetCode = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoAfi")),
                        fixedAssetName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreAfi")),
                        odometer = cursor.getString(cursor.getColumnIndexOrThrow("nKmCom")),
                        workerCode = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoTra")),
                        workerName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreTra")),
                        automatic = cursor.getInt(cursor.getColumnIndexOrThrow("cManualCom")),
                        combustible = cursor.getString(cursor.getColumnIndexOrThrow("cTipoCom")),
                        combustibleName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreCom")),
                        liters = cursor.getString(cursor.getColumnIndexOrThrow("nLitrosCom")),
                        field = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoCam")),
                        fieldName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreCam")),
                        activity = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoAct")),
                        activityName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreAct")),
                        zoneCode = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoZon")),
                        cCodigoUsu = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoUsu")),
                        isSynced = cursor.getInt(cursor.getColumnIndexOrThrow("isSynced")),
                        nPrecioCom = cursor.getString(cursor.getColumnIndexOrThrow("nPrecioCom"))
                    )
                    records.add(record)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
        }
        return if (records.isNotEmpty()) records else null
    }

    fun getUnsynchronizedRecords(): List<RecordModel>? {
        val db = this.writableDatabase

        val cursor = db.query(
            "z_gencontrolcomb",
            null,
            "isSynced = 0",
            null,
            null,
            null,
            null,
            null
        )

        val records = mutableListOf<RecordModel>()

        try {
            if (cursor.moveToFirst()) {
                do {
                    val record = RecordModel(
                        cControlCom = cursor.getLong(cursor.getColumnIndexOrThrow("cControlCom")),
                        date = cursor.getString(cursor.getColumnIndexOrThrow("dConsumoCom")),
                        weekNumber = cursor.getString(cursor.getColumnIndexOrThrow("cSemanaCom")),
                        fixedAssetCode = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoAfi")),
                        fixedAssetName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreAfi")),
                        odometer = cursor.getString(cursor.getColumnIndexOrThrow("nKmCom")),
                        workerCode = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoTra")),
                        workerName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreTra")),
                        automatic = cursor.getInt(cursor.getColumnIndexOrThrow("cManualCom")),
                        combustible = cursor.getString(cursor.getColumnIndexOrThrow("cTipoCom")),
                        combustibleName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreCom")),
                        liters = cursor.getString(cursor.getColumnIndexOrThrow("nLitrosCom")),
                        field = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoCam")),
                        fieldName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreCam")),
                        activity = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoAct")),
                        activityName = cursor.getString(cursor.getColumnIndexOrThrow("vNombreAct")),
                        zoneCode = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoZon")),
                        cCodigoUsu = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoUsu")),
                        isSynced = cursor.getInt(cursor.getColumnIndexOrThrow("isSynced")),
                        nPrecioCom = cursor.getString(cursor.getColumnIndexOrThrow("nPrecioCom"))
                    )
                    records.add(record)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
        }
        return if (records.isNotEmpty()) records else null
    }

    fun updateRecord(
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
        nPrecioCom: String,
    ): Int? {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("dConsumoCom", date)
            put("cSemanaCom", weekNumber)
            put("cCodigoAfi", fixedAssetCode)
            put("vNombreAfi", fixedAssetName)
            put("nKmCom", odometer)
            put("cCodigoTra", workerCode)
            put("vNombreTra", workerName)
            put("cManualCom", automatic)
            put("cTipoCom", combustible)
            put("vNombreCom", combustibleName)
            put("nLitrosCom", liters)
            put("cCodigoCam", field)
            put("vNombreCam", fieldName)
            put("cCodigoAct", activity)
            put("vNombreAct", activityName)
            put("cCodigoZon", zoneCode)
            put("cCodigoUsu", cCodigoUsu)
            put("isSynced", isSynced)
            put("nPrecioCom", nPrecioCom)
        }

        return try {
            db.update("z_gencontrolcomb", values, "cControlCom = ?", arrayOf(cControlCom.toString()))
        } catch (e: Exception) {
            Log.e("Database Error", "Error updating record: ${e.message}")
            0
        } finally {
            db.close()
        }
    }

    //Credentials
    fun getUserByCodeAndPassword(cCodigoUsu: String, vPasswordUsu: String): LoginModel? {
        val db = this.writableDatabase
        return try {
            db.query(
                "genlogin",
                null,
                "cCodigoUsu = ? AND vPasswordUsu = ?",
                arrayOf(cCodigoUsu, vPasswordUsu),
                null,
                null,
                null,
                "1"
            ).use { cursor ->
                if (cursor.moveToFirst()) {
                    LoginModel(
                        controlLog = cursor.getLong(cursor.getColumnIndexOrThrow("controlLog")),
                        vNombreUsu = cursor.getString(cursor.getColumnIndexOrThrow("vNombreUsu")),
                        cCodigoUsu = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoUsu")),
                        vPasswordUsu = cursor.getString(cursor.getColumnIndexOrThrow("vPasswordUsu"))
                    )
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("DBError", "Error fetching user: ${e.message}")
            null
        }
    }

    fun getAllUsers(): List<LoginModel> {
        val db = this.readableDatabase
        val users = mutableListOf<LoginModel>()

        return try {
            db.query(
                "genlogin",
                null,
                null,
                null,
                null,
                null,
                null
            ).use { cursor ->
                while (cursor.moveToNext()) {
                    val user = LoginModel(
                        controlLog = cursor.getLong(cursor.getColumnIndexOrThrow("controlLog")),
                        vNombreUsu = cursor.getString(cursor.getColumnIndexOrThrow("vNombreUsu")),
                        cCodigoUsu = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoUsu")),
                        vPasswordUsu = cursor.getString(cursor.getColumnIndexOrThrow("vPasswordUsu"))
                    )
                    users.add(user)
                    Log.d("DBUSER", "User: $user")
                }
            }
            users
        } catch (e: Exception) {
            Log.e("DBError", "Error fetching users: ${e.message}")
            emptyList()
        }
    }

    fun insertUser(
        vNombreUsu: String,
        cCodigoUsu: String,
        vPasswordUsu: String
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("vNombreUsu", vNombreUsu)
            put("cCodigoUsu", cCodigoUsu)
            put("vPasswordUsu", vPasswordUsu)
        }

        return db.insert("genlogin", null, values).also {
            db.close()
        }
    }

    fun deleteAllUsers() {
        val db = this.writableDatabase
        try {
            db.execSQL("DELETE FROM genlogin")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }
}