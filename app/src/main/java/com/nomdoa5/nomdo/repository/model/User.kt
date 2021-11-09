package com.nomdoa5.nomdo.repository.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int,
    val name: String? = null,
    val email: String? = null,
    @SerializedName("email_verified_at")
    val emailVerifiedAt: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null,
) : Parcelable
