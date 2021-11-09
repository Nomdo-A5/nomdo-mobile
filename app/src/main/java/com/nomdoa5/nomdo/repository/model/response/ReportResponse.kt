package com.nomdoa5.nomdo.repository.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReportResponse(
    @SerializedName("id_report")
    val idReport: Int,
    @SerializedName("report_name")
    val reportName: String? = null,
) : Parcelable
