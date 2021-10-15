package com.nomdoa5.nomdo.repository.model.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginRequest(
    val email: String? = null,
    val password: String? = null,
) : Parcelable
