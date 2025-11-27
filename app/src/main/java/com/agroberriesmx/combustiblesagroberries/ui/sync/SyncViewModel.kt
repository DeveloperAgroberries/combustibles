package com.agroberriesmx.combustiblesagroberries.ui.sync

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agroberriesmx.combustiblesagroberries.data.local.CombustiblesLocalDBService
import com.agroberriesmx.combustiblesagroberries.data.network.request.SyncRequest
import com.agroberriesmx.combustiblesagroberries.domain.RecordsRepository
import com.agroberriesmx.combustiblesagroberries.domain.model.FormattedRecordsModel
import com.agroberriesmx.combustiblesagroberries.domain.model.LoginModel
import com.agroberriesmx.combustiblesagroberries.domain.model.RecordModel
import com.agroberriesmx.combustiblesagroberries.domain.usecase.LoginsUseCase
import com.agroberriesmx.combustiblesagroberries.domain.usecase.UploadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val loginUseCase: LoginsUseCase,
    private val uploadUseCase: UploadUseCase,
    private val databaseService: CombustiblesLocalDBService,
    private val recordsRepository: RecordsRepository
): ViewModel() {
    private var _state = MutableLiveData<SyncState>(SyncState.Waiting)
    val state: LiveData<SyncState> get() = _state

    private var _pendingRecords = MutableLiveData<List<RecordModel>>()
    val pendingRecords: LiveData<List<RecordModel>> get() = _pendingRecords

    fun sync(token: String){
        viewModelScope.launch {
            _state.value = SyncState.Loading
            try {
                val syncRequest = SyncRequest(token, data = emptyList())
                val response: List<LoginModel>? = loginUseCase(syncRequest)
                if(response != null && response.isNotEmpty()){
                    databaseService.deleteAllUsers()
                    val insertResults = databaseService.insertUsers(response)
                    if (insertResults.all{ it != null }) {
                        _state.value = SyncState.Success(response)
                    } else {
                        _state.value = SyncState.Error("Fallo la sincronizacion al insertar algunos usuarios")
                    }
                } else {
                    _state.value = SyncState.Error("Fallo la sincronizacion de catalogos")
                }
            } catch (e:Exception) {
                _state.value = SyncState.Error(e.message ?: "Ha ocurrido un error")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun upload(){
        viewModelScope.launch {
            _state.value = SyncState.Loading
            try {
                val localData = recordsRepository.getUnsynchronizedRecords()

                if (localData != null){
                    val transformedData: List<FormattedRecordsModel> = localData.map { register ->
                        FormattedRecordsModel(
                            dConsumoCom = register.date,
                            cSemanaCom = register.weekNumber,
                            cNumeconAfi = register.fixedAssetCode,
                            nKmCom = register.odometer.toInt(),
                            cCodigoTra = register.workerCode,
                            vNombreTra = register.workerName,
                            cManualCom = register.automatic.toString(),
                            cTipoCom = register.combustible,
                            nLitrosCom = register.liters.toFloat(),
                            nPrecioCom = register.nPrecioCom.toFloat(),
                            cCodigoCam = register.field,
                            cCodigoAct = register.activity,
                            cCodigoZon = register.zoneCode,
                            cCodigoUsu = register.cCodigoUsu,
                            dCreacionCom = LocalDateTime.now().toString()
                        )
                    }
                    val response = uploadUseCase(transformedData)

                    if(response == "Ok"){
                        localData.forEach{record ->
                            record.isSynced = 1
                            recordsRepository.updateRecord(record)
                        }
                        _state.value=SyncState.UploadSuccess("Datos enviados correctamente")
                        loadRecords()
                    } else {
                        if (response == "Unauthorized"){
                            _state.value = SyncState.Error("No cuentas con un token para enviar los datos, cierra e inicia sesion y vuelve a intentarlo, por favor.")
                        } else {
                            _state.value = SyncState.Error(response)
                        }
                    }
                } else {
                    _state.value = SyncState.Error("No hay nada que enviar")
                }
            } catch (e: Exception) {
                _state.value = SyncState.Error(e.message ?: "Ha ocurrido un error")
            }
        }
    }

    fun loadRecords(){
        viewModelScope.launch {
            val records = recordsRepository.listUnsynchronizedRecords()
            _pendingRecords.value = records ?: emptyList()
        }
    }

    fun clearState() {
        _state.value = SyncState.Waiting
    }
}