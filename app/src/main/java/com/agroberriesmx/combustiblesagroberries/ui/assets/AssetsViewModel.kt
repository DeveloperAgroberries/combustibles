package com.agroberriesmx.combustiblesagroberries.ui.assets

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agroberriesmx.combustiblesagroberries.domain.model.FixedAssetModel
import com.agroberriesmx.combustiblesagroberries.domain.usecase.FetchAssetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AssetsViewModel @Inject constructor(private val fetchAssetUseCase: FetchAssetsUseCase) :
    ViewModel() {
    private var _state = MutableStateFlow<AssetState>(AssetState.Loading)
    val state: StateFlow<AssetState> = _state

    private var _filteredAssets = MutableLiveData<List<FixedAssetModel>?>(emptyList())
    val filteredAssets: MutableLiveData<List<FixedAssetModel>?> = _filteredAssets

    fun fecthAllAssets() {
        viewModelScope.launch {
            try {
                _state.value = AssetState.Loading
                val result = withContext(Dispatchers.IO) { fetchAssetUseCase("") }
                if (!result.isNullOrEmpty()) {
                    _state.value = AssetState.SuccessAsset(result)
                    _filteredAssets.postValue(result)
                } else {
                    _state.value = AssetState.Error("No se econtraron activos fijos")
                }
            } catch (e: Exception) {
                _state.value = AssetState.Error("Error al obtener los acticos: ${e.message}")
            }
        }
    }

    fun searchAssets(asset: String) {
        viewModelScope.launch {
            try {
                _state.value = AssetState.Loading
                val result = withContext(Dispatchers.IO) { fetchAssetUseCase(asset) }
                if (!result.isNullOrEmpty()) {
                    _state.value = AssetState.SuccessAsset(result)
                    _filteredAssets.postValue(result)
                } else {
                    _state.value = AssetState.Error("Activo Fijo no encontrado")
                }
            } catch (e: Exception) {
                _state.value =
                    AssetState.Error("Error al obtener los datos del activo fijo: ${e.message}")
            }
        }
    }
}