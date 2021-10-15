package com.nomdoa5.nomdo.repository.model.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterRequest(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    @SerializedName("password_confirmation")
    val passwordConfirmation: String? = null,
) : Parcelable
