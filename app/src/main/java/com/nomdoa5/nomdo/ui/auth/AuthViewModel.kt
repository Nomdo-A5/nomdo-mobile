package com.nomdoa5.nomdo.ui.auth

import androidx.lifecycle.*
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.User
import com.nomdoa5.nomdo.repository.model.request.auth.LoginRequest
import com.nomdoa5.nomdo.repository.model.request.auth.RegisterRequest
import com.nomdoa5.nomdo.repository.model.response.auth.LoginResponse
import com.nomdoa5.nomdo.repository.model.response.auth.LogoutResponse
import com.nomdoa5.nomdo.repository.model.response.UserResponse
import com.nomdoa5.nomdo.repository.model.response.auth.RegisterResponse
import com.nomdoa5.nomdo.repository.remote.ApiService
import com.nomdoa5.nomdo.repository.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel(private val pref: UserPreferences) : ViewModel() {
    private val _loginState = MutableStateFlow<LoadingState>(LoadingState.Empty)
    val loginState: StateFlow<LoadingState> = _loginState
    private val _registerState = MutableStateFlow<LoadingState>(LoadingState.Empty)
    val registerState: StateFlow<LoadingState> = _registerState
    private val logoutState = MutableLiveData<Boolean>()
    private val user = MutableLiveData<User>()
    private val userState = MutableLiveData<Boolean>()

    fun login(account: LoginRequest) {
        _loginState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.login(account)

        requestCall.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                val statusCode = response.body()!!.statusCode
                val responseToken = response.body()!!.accessToken
                val message = response.body()!!.message

                when {
                    statusCode!! == 200 -> {
                        saveAuthToken(responseToken!!)
                        _loginState.value = LoadingState.Success
                    }
                    else -> {
                        _loginState.value = LoadingState.Error(message!!)
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _loginState.value = LoadingState.Error("onFailure Server")
            }
        })
    }

    fun setUser(token: String) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getUser(token = "Bearer $token")

        requestCall.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                user.postValue(response.body()!!.user)
                userState.postValue(true)
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                userState.postValue(false)
            }
        })
    }

    fun logout(token: String) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.logout(token = "Bearer $token")

        requestCall.enqueue(object : Callback<LogoutResponse> {

            override fun onResponse(
                call: Call<LogoutResponse>,
                response: Response<LogoutResponse>
            ) {
                logoutState.postValue(true)
            }

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                logoutState.postValue(false)
            }
        })
    }

    fun register(newAccount: RegisterRequest) {
        _registerState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.register(newAccount)

        requestCall.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                when {
                    response.code() == 200 -> {
                        _registerState.value = LoadingState.Success
                    }
                    else -> _registerState.value = LoadingState.Error("Register Failed!")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _registerState.value = LoadingState.Error("onFailure Server Error")
            }
        })
    }

    fun getUser(): LiveData<User> {
        return user
    }

    fun getUserState(): LiveData<Boolean> {
        return userState
    }

    fun getLogoutState(): LiveData<Boolean> {
        return logoutState
    }

    fun getAuthToken(): LiveData<String?> {
        return pref.getAuthToken().asLiveData()
    }

    fun saveAuthToken(token: String) {
        viewModelScope.launch {
            pref.saveAuthToken(token)
        }
    }

    fun deleteAuthToken() {
        viewModelScope.launch {
            pref.deleteAuthToken()
        }
    }
}