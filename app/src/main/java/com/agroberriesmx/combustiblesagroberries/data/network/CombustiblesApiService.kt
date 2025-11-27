package com.agroberriesmx.combustiblesagroberries.data.network

import com.agroberriesmx.combustiblesagroberries.data.network.request.LoginRequest
import com.agroberriesmx.combustiblesagroberries.data.network.response.FetchAssetsResponse
import com.agroberriesmx.combustiblesagroberries.data.network.response.FieldsApiResponse
import com.agroberriesmx.combustiblesagroberries.data.network.response.FixedAssetApiResponse
import com.agroberriesmx.combustiblesagroberries.data.network.response.LoginResponse
import com.agroberriesmx.combustiblesagroberries.data.network.response.LoginsResponse
import com.agroberriesmx.combustiblesagroberries.data.network.response.PhotoUploadResponse
import com.agroberriesmx.combustiblesagroberries.data.network.response.UploadResponse
import com.agroberriesmx.combustiblesagroberries.data.network.response.WorkerApiResponse
import com.agroberriesmx.combustiblesagroberries.domain.model.FixedAssetModel
import com.agroberriesmx.combustiblesagroberries.domain.model.FormattedRecordsModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
// --- ¡Nuevas importaciones necesarias para la carga de archivos! ---
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.Part
import com.google.gson.annotations.SerializedName // Important for mapping JSON keys to Kotlin properties

interface CombustiblesApiService {
    @POST("LoginUser")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @GET("ListLogins")
    suspend fun loginsData(): LoginsResponse

    @POST("SaveFuelRegisters")
    suspend fun uploadData(@Body records: List<FormattedRecordsModel>): Response<UploadResponse>

    @GET("FixedAssetData/{CodigoAfi}")
    suspend fun fixedAssetData(@Path("CodigoAfi") fixedAsset: String): FixedAssetApiResponse

    @GET("FetchAssets/{assetCode}")
    suspend fun fetchAssets(@Path("assetCode") assetCode: String): FetchAssetsResponse?

    @GET("WorkerData/{CodigoTra}")
    suspend fun workerData(@Path("CodigoTra") worker: String): WorkerApiResponse

    @GET("ListFields")
    suspend fun listFields(): FieldsApiResponse

    // NUEVO: Endpoint para la carga de fotos (Activo Fijo y Bomba)
    @Multipart // Indica que esta solicitud es de tipo multipart/form-data
    @POST("upload_photo_AFBOMBA/{subfolderName}") // Endpoint con variable de ruta
    suspend fun uploadPhoto(
        @Path("subfolderName") subfolderName: String, // La variable de ruta para el nombre de la subcarpeta
        @Part file: MultipartBody.Part // La parte del cuerpo para el archivo de la foto
    ): Response<PhotoUploadResponse>
}

