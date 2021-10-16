package com.nomdoa5.nomdo.repository.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @SerializedName("id_user")
    val idUser: Int,
    val name: String? = null,
    val email: String? = null,
    val password: String? = null
) : Parcelable
