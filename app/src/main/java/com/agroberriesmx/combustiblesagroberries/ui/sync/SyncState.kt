package com.agroberriesmx.combustiblesagroberries.ui.sync

import com.agroberriesmx.combustiblesagroberries.domain.model.LoginModel

sealed class SyncState {
    data object Loading: SyncState()
    data object Waiting: SyncState()

    data class Error(val message: String): SyncState()
    data class Success(val success: List<LoginModel>): SyncState()
    data class UploadSuccess(val message: String): SyncState()
}