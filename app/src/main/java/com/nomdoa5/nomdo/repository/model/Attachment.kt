package com.nomdoa5.nomdo.repository.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attachment(
    val id: Int,
    @SerializedName("file_path")
    val filePath: String,
    @SerializedName("balance_id")
    val balanceId: Int,
) : Parcelable
