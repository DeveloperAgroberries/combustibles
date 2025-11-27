package com.agroberriesmx.combustiblesagroberries.domain.usecase

import com.agroberriesmx.combustiblesagroberries.domain.Repository // Importa tu interfaz de repositorio
import com.agroberriesmx.combustiblesagroberries.data.network.response.PhotoUploadResponse // Importa tu clase de respuesta de la API
import okhttp3.MultipartBody // Necesario para el tipo de dato de la foto
import retrofit2.Response // Necesario para el tipo de respuesta de Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UploadPhotoUseCase @Inject constructor(
    private val repository: Repository // <-- Inyecta la interfaz 'Repository'
) {
    suspend operator fun invoke(subfolderName: String, photoPart: MultipartBody.Part): Response<PhotoUploadResponse> {
        return repository.uploadPhoto(subfolderName, photoPart)
    }
}