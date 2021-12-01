package com.nomdoa5.nomdo.repository.model.response.balance

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.nomdoa5.nomdo.repository.model.Balance
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReportResponse(
    val balance: ArrayList<Balance>
) : Parcelable
