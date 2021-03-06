package com.nomdoa5.nomdo.repository.model.request.balance

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateBalanceRequest(
    val id: Int?,
    val nominal: String? = null,
    @SerializedName("balance_description")
    val balanceDescription: String? = null,
    @SerializedName("is_income")
    val isIncome: Int? = null,
    val status: String? = null,
    val date: String? = null,
) : Parcelable