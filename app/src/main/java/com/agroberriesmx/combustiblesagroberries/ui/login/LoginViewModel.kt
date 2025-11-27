package com.agroberriesmx.combustiblesagroberries.ui.login

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agroberriesmx.combustiblesagroberries.data.local.CombustiblesLocalDBService
import com.agroberriesmx.combustiblesagroberries.data.network.request.LoginRequest
import com.agroberriesmx.combustiblesagroberries.domain.model.LoginModel
import com.agroberriesmx.combustiblesagroberries.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.security.MessageDigest
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val localDBService: CombustiblesLocalDBService,
    private val application: Application
) : ViewModel() {
    private var _state = MutableLiveData<LoginState>(LoginState.Waiting)
    val state: LiveData<LoginState> = _state

    private val context: Context get() = application.applicationContext
    private var authenticatedUser: LoginModel? = null

    @RequiresApi(Build.VERSION_CODES.M)
    fun login(userId: String, password: String, activeUser: String, creatorId: String) {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            try {
                if (isInternetAvailable(context)) {
                    val loginRequest = LoginRequest(userId, password, activeUser, creatorId)
                    loginUseCase(loginRequest)
                        .onSuccess { token -> _state.value = LoginState.Success(token) }
                        .onFailure { error -> _state.value = LoginState.Error("Error de acceso: ${error.message}") }
                } else {
//                    val md5Hash = password.toMD5()
//                    val user = localDBService.getUserByCodeAndPassword(userId, md5Hash)
//                    if (user != null) {
//                        authenticatedUser = user
//                        _state.value = LoginState.Success(null, isLocal = true)
//                    } else {
//                        _state.value = LoginState.Error("Credenciales incorrectas")
//                    }
                    _state.value =
                        LoginState.Error("No cuentas con conexion a internet, vuelve a intentarlo, por favor")
                }
            } catch (e: Exception) {
                _state.value = LoginState.Error(e.message ?: "Ha ocurrido un error inesperado")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun String.toMD5(): String {
        // Crear una instancia de MessageDigest para MD5
        val digest = MessageDigest.getInstance("MD5")
        // Calcular el hash y convertirlo a un arreglo de bytes
        val hashBytes = digest.digest(this.toByteArray())
        // Convertir los bytes a un formato hexadecimal
        return hashBytes.joinToString("") { String.format("%02x", it) }
    }
}