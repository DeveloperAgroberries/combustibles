package com.agroberriesmx.combustiblesagroberries.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agroberriesmx.combustiblesagroberries.domain.RecordsRepository
import com.agroberriesmx.combustiblesagroberries.domain.model.RecordModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListRecordsViewModel @Inject constructor(private val repository: RecordsRepository) :
    ViewModel() {
    private val _todayRecords = MutableLiveData<List<RecordModel>?>(emptyList())
    val todayRecords: MutableLiveData<List<RecordModel>?> = _todayRecords

    private val _allRecords = MutableLiveData<List<RecordModel>?>(emptyList())
    val allRecords: MutableLiveData<List<RecordModel>?> = _allRecords

    private val _filteredRecords = MutableLiveData<List<RecordModel>?>(emptyList())
    val filteredRecords: MutableLiveData<List<RecordModel>?> = _filteredRecords

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadTodayRecords()
        loadAllRecords()
    }

    fun loadTodayRecords() {
        viewModelScope.launch {
            try {
                _todayRecords.value = repository.getRecordsByDate()
                _filteredRecords.value = _todayRecords.value
            } catch (e: Exception) {
                _error.value = "Error al cargar los registros: ${e.message}"
            }
        }
    }

    fun loadAllRecords() {
        viewModelScope.launch {
            try {
                _allRecords.value = repository.getAllRecords()
            } catch (e: Exception) {
                _error.value = "Error al cargar los registros: ${e.message}"
            }
        }
    }

    fun searchRecords(query: String) {
        viewModelScope.launch {
            val currentRecords = if (query.isBlank()) {
                _todayRecords.value ?: emptyList()
            } else {
                _allRecords.value ?: emptyList()
            }

            val filtered = currentRecords.filter { record ->
                record.date.contains(query, ignoreCase = true)
            }

            _filteredRecords.value = filtered
        }
    }
}