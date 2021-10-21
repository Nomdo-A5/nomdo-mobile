package com.nomdoa5.nomdo.repository.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.nomdoa5.nomdo.repository.model.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserResponse(
    val user: ArrayList<User>,
) : Parcelable
