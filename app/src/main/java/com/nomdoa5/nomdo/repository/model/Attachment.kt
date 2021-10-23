package com.nomdoa5.nomdo.repository.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attachment(
    @SerializedName("id_attachment")
    val idAttachment: Int,
    @SerializedName("file_path")
    val filePath: String? = null,
) : Parcelable
