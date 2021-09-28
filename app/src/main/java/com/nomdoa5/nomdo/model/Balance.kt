package com.nomdoa5.nomdo.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Balance(
    @SerializedName("id_balance")
    val idBalance: Int,
    @SerializedName("status_income")
    val statusIncome: Boolean? = null,
    val nominal: Int? = null,
    val description: String? = null,
) : Parcelable
