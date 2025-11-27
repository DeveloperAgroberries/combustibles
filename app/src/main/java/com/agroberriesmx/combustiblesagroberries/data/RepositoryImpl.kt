package com.agroberriesmx.combustiblesagroberries.data

import android.util.Log
import com.agroberriesmx.combustiblesagroberries.data.network.CombustiblesApiService
import com.agroberriesmx.combustiblesagroberries.data.network.request.LoginRequest
import com.agroberriesmx.combustiblesagroberries.data.network.request.SyncRequest
import com.agroberriesmx.combustiblesagroberries.data.network.response.FieldsApiResponse
import com.agroberriesmx.combustiblesagroberries.data.network.response.LoginsResponse
import com.agroberriesmx.combustiblesagroberries.domain.Repository
import com.agroberriesmx.combustiblesagroberries.domain.model.FieldModel
import com.agroberriesmx.combustiblesagroberries.domain.model.FixedAssetModel
import com.agroberriesmx.combustiblesagroberries.domain.model.FormattedRecordsModel
import com.agroberriesmx.combustiblesagroberries.domain.model.LoginModel
import com.agroberriesmx.combustiblesagroberries.domain.model.TokenModel
import com.agroberriesmx.combustiblesagroberries.domain.model.WorkerModel
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

// --- Nuevas importaciones necesarias para la carga de fotos ---
import com.agroberriesmx.combustiblesagroberries.data.network.response.PhotoUploadResponse
import okhttp3.MultipartBody
import retrofit2.Response // Importar Response de Retrofit
// --- Fin nuevas importaciones ---

class RepositoryImpl @Inject constructor(
    private val apiService: CombustiblesApiService
) : Repository {
    companion object {
        private const val APP_INFO_TAG_KEY = "ControlCombustibles"
    }

    override suspend fun getToken(loginRequest: LoginRequest): Result<TokenModel> {
        return runCatching { apiService.login(loginRequest).toDomain() }
    }

    override suspend fun getLogins(syncRequest: SyncRequest): List<LoginModel>? {
        return kotlin.runCatching {
            val logins: LoginsResponse = apiService.loginsData()
            if (logins.logins.isEmpty()) {
                throw Exception("No hay logins disponibles")
            }
            logins.logins.mapIndexed { index, loginResponseItem ->
                loginResponseItem.toDomain(controlLog = index.toLong())
            }
        }.onFailure {
            Log.i(APP_INFO_TAG_KEY, "Ha ocurrido un error ${it.message}")
        }.getOrNull()
    }

    override suspend fun uploadRecords(records: List<FormattedRecordsModel>): Pair<String?, Int?> {
        return runCatching {
            val response = apiService.uploadData(records)
            if (response.isSuccessful) {
                val responseBody = response.body()
                responseBody?.let {
                    Pair(it.message, response.code())
                } ?: Pair(null, response.code())
            } else {
                Pair(null, response.code())
            }
        }.getOrElse { exception ->
            val errorCode = when (exception) {
                is HttpException -> exception.code()
                is IOException -> 1
                else -> 2
            }
            Pair(null, errorCode)
        }
    }

    override suspend fun getFixedAssetData(fixedAsset: String): FixedAssetModel? {
        return runCatching { apiService.fixedAssetData(fixedAsset) }
            .map { response ->
                return response.response.toDomain()
            }
            .getOrElse {
                Log.i(APP_INFO_TAG_KEY, "Ha ocurrido un error ${it.message}")
                null
            }
    }

    override suspend fun fetchAssets(asset: String): List<FixedAssetModel>? {
        return runCatching { apiService.fetchAssets(asset) }
            .map { response ->
                response?.data?.map { it.toDomain() } ?: emptyList()
            }
            .getOrElse {
                Log.i(APP_INFO_TAG_KEY, "Ha ocurrido un error: ${it.message}")
                null
            }
    }

    override suspend fun getWorkerData(worker: String): WorkerModel? {

        return runCatching { apiService.workerData(worker) }
            .map { response ->
                return response.response.toDomain()
            }
            .getOrElse {
                Log.i(APP_INFO_TAG_KEY, "Ha ocurrido un error ${it.message}")
                null
            }
    }

    override suspend fun getFields(): List<FieldModel>? {
        return runCatching {
            val fields: FieldsApiResponse = apiService.listFields()
            if (fields.response.isEmpty()) {
                throw Exception("No hay logins disponibles")
            }
            fields.response.map { fieldsResponse ->
                fieldsResponse.toDomain()
            }
        }.onFailure {
            Log.i(APP_INFO_TAG_KEY, "Ha ocurrido un error ${it.message}")
        }.getOrNull()
    }

    // --- ¡LA IMPLEMENTACIÓN DE LA FUNCIÓN DE SUBIDA DE FOTOS VA AQUÍ! ---
    override suspend fun uploadPhoto(subfolderName: String, photoPart: MultipartBody.Part): Response<PhotoUploadResponse> {
        return apiService.uploadPhoto(subfolderName, photoPart)
    }
}