package com.nomdoa5.nomdo.repository.model.request.balance

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateBalanceRequest(
    val id : Int?,
    @SerializedName("balance_name")
    val balanceName: String? = null,
) : Parcelable