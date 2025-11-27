package com.agroberriesmx.combustiblesagroberries.ui.assetdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agroberriesmx.combustiblesagroberries.domain.usecase.GetFixedAssetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AssetDetailViewModel @Inject constructor(private val getFixedAssetUseCase: GetFixedAssetUseCase) :
    ViewModel() {
    private val _state = MutableLiveData<AssetDetailState>()
    val state: LiveData<AssetDetailState> = _state

    fun getAsset(numecon: String) {
        viewModelScope.launch {
            try {
                _state.value = AssetDetailState.Loading
                val result = withContext(Dispatchers.IO) { getFixedAssetUseCase(numecon) }
                if (result != null) {
                    _state.value = AssetDetailState.Success(
                        /*result.cCodigoAfi,
                        result.cNumeconAfi,
                        result.vNombreAfi,
                        result.vNumserieAfi,
                        result.vObservacionAfi,
                        result.vPlacasAfi,
                        result.nKmAfi,*/
                        result.numecon,
                        result.nombreAfi,
                        result.placas,
                        result.fecha,
                        result.litros,
                        result.campo,
                        result.zona,
                        result.actividad,
                    )
                } else {
                    _state.value = AssetDetailState.Error("Activo Fijo no encontrado")
                }
            } catch (e: Exception) {
                _state.value =
                    AssetDetailState.Error("Error al obtener los datos del activo fijo: ${e.message}")
            }
        }
    }
}