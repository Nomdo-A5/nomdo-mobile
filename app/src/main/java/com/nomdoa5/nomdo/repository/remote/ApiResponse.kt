package com.nomdoa5.nomdo.repository.remote

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.request.LoginRequest
import com.nomdoa5.nomdo.repository.model.request.RegisterRequest
import com.nomdoa5.nomdo.repository.model.Task
import com.nomdoa5.nomdo.repository.model.response.LoginResponse
import com.nomdoa5.nomdo.repository.model.response.LogoutResponse
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import retrofit2.Call
import retrofit2.http.*

interface ApiResponse {

    @GET("task")
    fun getTask(): Call<Task>

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("logout")
    fun logout(@Header("Authorization") token: String): Call<LogoutResponse>

    @POST("register")
    fun register(@Body register: RegisterRequest): Call<RegisterRequest>

//    @GET("users/{username}")
//    @Headers("Authorization: ${BuildConfig.GITHUB_TOKEN}")
//    fun getDetailUsers(
//        @Path("username") username: String,
//    ): Call<UserDetail>
//
//    @GET("users/{username}/following")
//    @Headers("Authorization: ${BuildConfig.GITHUB_TOKEN}")
//    fun getFollowingUsers(
//        @Path("username") username: String,
//    ): Call<ArrayList<User>>
//
//    @GET("users/{username}/followers")
//    @Headers("Authorization: ${BuildConfig.GITHUB_TOKEN}")
//    fun getFollowersUsers(
//        @Path("username") username: String,
//    ): Call<ArrayList<User>>
}