package com.nomdoa5.nomdo.repository.model.response.balance

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.nomdoa5.nomdo.repository.model.Balance
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReportOverviewResponse(
    @SerializedName("income_balance")
    val incomeBalance: Double,
    @SerializedName("outcome_balance")
    val outcomeBalance: Double,
    @SerializedName("total_balance")
    val totalBalance: Double,
) : Parcelable