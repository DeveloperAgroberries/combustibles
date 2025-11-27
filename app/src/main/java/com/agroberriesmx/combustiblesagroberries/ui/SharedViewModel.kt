package com.agroberriesmx.combustiblesagroberries.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class SharedViewModel @Inject constructor() : ViewModel() {
    private val _recordAdded = MutableLiveData<Boolean>()
    val recordAdded: LiveData<Boolean> get() = _recordAdded

    private val _assetAdded = MutableLiveData<Boolean>()
    val assetAdded: LiveData<Boolean> get() = _assetAdded

    fun addRecord() {
        _recordAdded.value = true
    }

    fun addAsset() {
        _recordAdded.value = true
    }

    fun resetRecordAdded() {
        _recordAdded.value = false
    }

    fun resetAssetAdded() {
        _recordAdded.value = false
    }


}