package com.nomdoa5.nomdo.repository.remote

import com.nomdoa5.nomdo.repository.model.request.LoginRequest
import com.nomdoa5.nomdo.repository.model.request.RegisterRequest
import com.nomdoa5.nomdo.repository.model.Task
import com.nomdoa5.nomdo.repository.model.request.BoardRequest
import com.nomdoa5.nomdo.repository.model.response.WorkspaceResponse
import com.nomdoa5.nomdo.repository.model.response.LoginResponse
import com.nomdoa5.nomdo.repository.model.response.LogoutResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiResponse {

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("logout")
    fun logout(@Header("Authorization") token: String): Call<LogoutResponse>

    @POST("register")
    fun register(@Body register: RegisterRequest): Call<RegisterRequest>

    @GET("workspace")
    fun getWorkspace(@Header("Authorization") token: String) : Call<WorkspaceResponse>

    @GET("boards")
    fun getBoard(@Header("Authorization") token: String, @Body boardRequest: BoardRequest) : Call<BoardRequest>

    @GET("task")
    fun getTask(@Header("Authorization") token: String): Call<Task>




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