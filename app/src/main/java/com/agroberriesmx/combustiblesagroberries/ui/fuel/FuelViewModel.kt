package com.agroberriesmx.combustiblesagroberries.ui.fuel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agroberriesmx.combustiblesagroberries.domain.usecase.GetFieldsUseCase
import com.agroberriesmx.combustiblesagroberries.domain.usecase.GetFixedAssetUseCase
import com.agroberriesmx.combustiblesagroberries.domain.usecase.GetWorkerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// --- ¡Nuevas importaciones necesarias! ---
import com.agroberriesmx.combustiblesagroberries.domain.usecase.UploadPhotoUseCase // Importa tu nuevo Use Case
import com.agroberriesmx.combustiblesagroberries.data.network.response.PhotoUploadResponse // Importa tu clase de respuesta de la API
import okhttp3.MultipartBody // Necesario para el tipo de dato de la foto
import retrofit2.Response // Necesario para el tipo de respuesta de Retrofit
// --- Fin de nuevas importaciones ---

@HiltViewModel
class FuelViewModel @Inject constructor(
    private val getFixedAssetUseCase: GetFixedAssetUseCase,
    private val getWorkerUseCase: GetWorkerUseCase,
    private val getFieldsUseCase: GetFieldsUseCase,
    private val uploadPhotoUseCase: UploadPhotoUseCase // <--- ¡Asegúrate de que está aquí!
) : ViewModel() {
    private var _state = MutableStateFlow<FuelState>(FuelState.Loading)
    val state: StateFlow<FuelState> = _state

    fun getFixedAssetData(fixedAsset: String) {
        viewModelScope.launch {
            try {
                _state.value = FuelState.Loading
                val result = withContext(Dispatchers.IO) { getFixedAssetUseCase(fixedAsset) }
                if (result != null) {
                    _state.value = FuelState.SuccessFixedAsset(
                        result
                    )
                } else {
                    _state.value = FuelState.Error("Activo Fijo no encontrado")
                }
            } catch (e: Exception) {
                _state.value =
                    FuelState.Error("Error al obtener los datos del activo fijo: ${e.message}")
            }
        }
    }

    fun getWorkerData(worker: String) {
        viewModelScope.launch {
            try {
                _state.value = FuelState.Loading
                val result = withContext(Dispatchers.IO) { getWorkerUseCase(worker) }
                if (result != null) {
                    _state.value = FuelState.SuccessWorker(
                        result
                    )
                } else {
                    _state.value = FuelState.Error("Trabajador no encontrado")
                }
            } catch (e: Exception) {
                _state.value =
                    FuelState.Error("Error al obtener los datos del trabajador: ${e.message}")
            }
        }
    }

    fun getFieldsData() {
        viewModelScope.launch {
            try {
                _state.value = FuelState.Loading
                val result = withContext(Dispatchers.IO) { getFieldsUseCase() }
                if (result != null) {
                    _state.value = FuelState.SuccessField(
                        result
                    )
                } else {
                    _state.value = FuelState.Error("Campos no encontrados")
                }
            } catch (e: Exception) {
                _state.value =
                    FuelState.Error("Error al obtener los campos: ${e.message}")
            }
        }
    }

    // --- ¡NUEVA FUNCIÓN PARA SUBIR LA FOTO EN EL VIEWMODEL! ---
    // Esta función será llamada desde tu FuelFragment.
    suspend fun uploadPhotoToApi(subfolderName: String, photoPart: MultipartBody.Part): Response<PhotoUploadResponse> {
        // Ejecutamos el Use Case de subida de fotos
        // El 'uploadPhotoUseCase' es un 'operator fun invoke', así que se llama directamente
        return uploadPhotoUseCase(subfolderName, photoPart)
    }
}