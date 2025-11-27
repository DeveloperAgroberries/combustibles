package com.agroberriesmx.combustiblesagroberries.ui.recordsdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agroberriesmx.combustiblesagroberries.domain.usecase.RecordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ListRecordDetailViewModel @Inject constructor(private val getRecordUseCase: RecordUseCase) :
    ViewModel() {
    private val _state = MutableLiveData<ListRecordDetailState>()
    val state: LiveData<ListRecordDetailState> = _state

    fun getRecord(cControlCom: Long) {
        viewModelScope.launch {
            _state.value = ListRecordDetailState.Loading

            val result = withContext(Dispatchers.IO) {
                getRecordUseCase(cControlCom)
            }

            if (result != null) {
                _state.value = ListRecordDetailState.Success(
                    result.cControlCom,
                    result.date,
                    result.weekNumber,
                    result.fixedAssetCode,
                    result.fixedAssetName,
                    result.odometer,
                    result.workerCode,
                    result.workerName,
                    result.automatic,
                    result.combustible,
                    result.combustibleName,
                    result.liters,
                    result.nPrecioCom,
                    result.field,
                    result.fieldName,
                    result.activity,
                    result.activityName,
                    result.cCodigoUsu,
                    result.isSynced,
                )
            } else {
                _state.value =
                    ListRecordDetailState.Error("Ha ocurrido un error cargando los datos")
            }
        }
    }
}