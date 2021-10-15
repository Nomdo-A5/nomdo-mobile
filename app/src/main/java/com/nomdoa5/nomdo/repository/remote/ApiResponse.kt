package com.nomdoa5.nomdo.repository.remote

import com.nomdoa5.nomdo.model.Task
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiResponse {
    @GET("task")
        fun getTask(): Call<Task>

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