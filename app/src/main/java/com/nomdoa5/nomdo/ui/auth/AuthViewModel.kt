package com.nomdoa5.nomdo.ui.auth

import androidx.lifecycle.*
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.User
import com.nomdoa5.nomdo.repository.model.request.auth.LoginRequest
import com.nomdoa5.nomdo.repository.model.request.auth.RegisterRequest
import com.nomdoa5.nomdo.repository.model.response.LoginResponse
import com.nomdoa5.nomdo.repository.model.response.LogoutResponse
import com.nomdoa5.nomdo.repository.model.response.UserResponse
import com.nomdoa5.nomdo.repository.remote.ApiService
import com.nomdoa5.nomdo.repository.remote.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel(private val pref: UserPreferences) : ViewModel() {
    private val isRegisterSuccess = MutableLiveData<Boolean>()
    private val loginState = MutableLiveData<Boolean>()
    private val logoutState = MutableLiveData<Boolean>()
    private val user = MutableLiveData<User>()
    private val userState = MutableLiveData<Boolean>()

    fun login(account: LoginRequest) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.login(account)

        requestCall.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                val statusCode = response.body()!!.statusCode
                val responseToken = response.body()!!.accessToken

                if (statusCode!!.equals(200)) {
                    saveAuthToken(responseToken!!)
                    loginState.postValue(true)
                } else if (statusCode.equals(401)) {
                    loginState.postValue(false)
                } else {
                    loginState.postValue(false)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginState.postValue(false)
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
                deleteAuthToken()
                logoutState.postValue(true)
            }

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                deleteAuthToken()
                logoutState.postValue(false)
            }
        })
    }

    fun register(newAccount: RegisterRequest) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.register(newAccount)

        requestCall.enqueue(object : Callback<RegisterRequest> {
            override fun onResponse(
                call: Call<RegisterRequest>,
                response: Response<RegisterRequest>
            ) {
                if (response.code().equals(200)) {
                    isRegisterSuccess.postValue(true)
                } else if (response.code().equals(401))
                    isRegisterSuccess.postValue(false)
                else
                    isRegisterSuccess.postValue(false)
            }

            override fun onFailure(call: Call<RegisterRequest>, t: Throwable) {
                isRegisterSuccess.postValue(false)
            }
        })
    }

    fun getUser(): LiveData<User> {
        return user
    }

    fun getUserState(): LiveData<Boolean> {
        return userState
    }

    fun getRegisterStatus(): LiveData<Boolean> {
        return isRegisterSuccess
    }

    fun getLoginState(): LiveData<Boolean> {
        return loginState
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

    fun isAuthTokenSet(): Boolean {
        return !pref.getAuthToken().toString().isNullOrEmpty()
    }
}