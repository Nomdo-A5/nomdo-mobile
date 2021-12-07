package com.nomdoa5.nomdo.repository.model.response.auth

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResponse(
    @SerializedName("status_code")
    val statusCode: Int? = null,
    val message: String? = null,
    @SerializedName("access_token")
    val accessToken: String? = null,
    @SerializedName("token_type")
    val tokenType: String? = null,
) : Parcelable
