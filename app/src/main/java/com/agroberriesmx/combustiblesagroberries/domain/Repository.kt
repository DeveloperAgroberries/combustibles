package com.agroberriesmx.combustiblesagroberries.domain

import com.agroberriesmx.combustiblesagroberries.data.network.request.LoginRequest
import com.agroberriesmx.combustiblesagroberries.data.network.request.SyncRequest
import com.agroberriesmx.combustiblesagroberries.domain.model.FieldModel
import com.agroberriesmx.combustiblesagroberries.domain.model.FixedAssetModel
import com.agroberriesmx.combustiblesagroberries.domain.model.FormattedRecordsModel
import com.agroberriesmx.combustiblesagroberries.domain.model.LoginModel
import com.agroberriesmx.combustiblesagroberries.domain.model.TokenModel
import com.agroberriesmx.combustiblesagroberries.domain.model.WorkerModel
// --- ¡NUEVAS IMPORTACIONES NECESARIAS EN LA INTERFAZ! ---
import com.agroberriesmx.combustiblesagroberries.data.network.response.PhotoUploadResponse
import okhttp3.MultipartBody
import retrofit2.Response
// --- FIN NUEVAS IMPORTACIONES ---

interface Repository {
    suspend fun getToken(loginRequest: LoginRequest): Result<TokenModel>
    suspend fun getLogins(syncRequest: SyncRequest): List<LoginModel>?
    suspend fun uploadRecords(records: List<FormattedRecordsModel>): Pair<String?, Int?>
    suspend fun getFixedAssetData(fixedAsset: String): FixedAssetModel?
    suspend fun fetchAssets(asset: String): List<FixedAssetModel>?
    suspend fun getWorkerData(worker: String): WorkerModel?
    suspend fun getFields(): List<FieldModel>?
    // --- ¡AÑADE ESTA FUNCIÓN AQUÍ EN LA INTERFAZ! ---
    // Esta es la declaración que le dice a la interfaz que uploadPhoto debe existir
    suspend fun uploadPhoto(subfolderName: String, photoPart: MultipartBody.Part): Response<PhotoUploadResponse>
}