package com.nomdoa5.nomdo.repository.model.response.auth

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterResponse(
    @SerializedName("status_code")
    val statusCode: Int? = null,
    val message: String? = null,
) : Parcelable
