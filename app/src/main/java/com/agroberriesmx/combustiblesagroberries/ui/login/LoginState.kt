package com.agroberriesmx.combustiblesagroberries.ui.login

import com.agroberriesmx.combustiblesagroberries.domain.model.TokenModel

sealed class LoginState {
    data object Loading: LoginState()
    data object Waiting: LoginState()

    data class Error(val message: String): LoginState()
    data class Success(val success: TokenModel? = null, val isLocal: Boolean = false): LoginState()
}