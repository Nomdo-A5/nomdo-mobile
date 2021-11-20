package com.nomdoa5.nomdo.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nomdoa5.nomdo.repository.model.User
import com.nomdoa5.nomdo.repository.model.response.UserResponse
import com.nomdoa5.nomdo.repository.remote.ApiService
import com.nomdoa5.nomdo.repository.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.UnknownServiceException

class MainViewModel : ViewModel() {
    private val user = MutableLiveData<User>()
    private val userState = MutableLiveData<Boolean>()

    fun setUser(token: String) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getUser(token = "Bearer $token")

        requestCall.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if(!response.code().equals(500)){
                    user.postValue(response.body()!!.user)
                    userState.postValue(true)
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                userState.postValue(false)
            }
        })
    }

    fun getUser(): LiveData<User> {
        return user
    }

    fun getUserState(): LiveData<Boolean> {
        return userState
    }
}