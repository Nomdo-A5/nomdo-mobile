package com.nomdoa5.nomdo.repository.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Balance(
    @SerializedName("workspace_id")
    val workspaceId: Int?,
    val nominal: Int?,
    @SerializedName("balance_description")
    val balanceDescription: String? = null,
    @SerializedName("is_income")
    val isIncome: Int? = null,
) : Parcelable
