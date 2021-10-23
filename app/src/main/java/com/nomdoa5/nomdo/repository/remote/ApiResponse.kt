package com.nomdoa5.nomdo.repository.remote

import com.nomdoa5.nomdo.repository.model.Task
import com.nomdoa5.nomdo.repository.model.request.*
import com.nomdoa5.nomdo.repository.model.response.*
import retrofit2.Call
import retrofit2.http.*

interface ApiResponse {

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("user")
    fun getUser(@Header("Authorization") token: String): Call<UserResponse>

    @POST("logout")
    fun logout(@Header("Authorization") token: String): Call<LogoutResponse>

    @POST("register")
    fun register(@Body register: RegisterRequest): Call<RegisterRequest>

    @GET("workspace")
    fun getWorkspace(@Header("Authorization") token: String): Call<WorkspaceResponse>

    @POST("workspace")
    fun addWorkspace(
        @Header("Authorization") token: String,
        @Body workspace: WorkspaceRequest
    ): Call<WorkspaceResponse>

    @GET("boards")
    fun getBoard(
        @Header("Authorization") token: String,
        @Query("workspace_id") idWorkspace: String,
    ): Call<BoardResponse>

    @POST("boards")
    fun addBoard(
        @Header("Authorization") token: String,
        @Body board: BoardRequest
    ): Call<BoardResponse>

    @GET("task")
    fun getTask(
        @Header("Authorization") token: String, @Query("board_id") idBoard: String,
    ): Call<TaskResponse>

    @GET("task")
    fun getDetailTask(
        @Header("Authorization") token: String, @Query("id") id: String,
    ): Call<Task>

    @POST("task")
    fun addTask(@Header("Authorization") token: String, @Body task: TaskRequest): Call<TaskResponse>

    @PATCH("task")
    fun updateTask(@Header("Authorization") token: String, @Body task: TaskRequest): Call<TaskResponse>

}