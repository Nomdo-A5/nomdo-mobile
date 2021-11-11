package com.nomdoa5.nomdo.repository.model.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReportRequest(
    @SerializedName("report_name")
    val reportName: String? = null,
    @SerializedName("workspace_id")
    val workspaceId: Int,
) : Parcelable
