package com.nomdoa5.nomdo.repository.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Board(
    val id: Int,
    @SerializedName("board_name")
    val boardName: String? = null,
    @SerializedName("board_description")
    val boardDescription: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val openedAt: String? = null,
    @SerializedName("workspace_id")
    val idWorkspace: Int?=null
) : Parcelable
