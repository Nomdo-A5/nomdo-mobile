package com.nomdoa5.nomdo.repository.model.request.board

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BoardRequest(
    @SerializedName("board_name")
    val boardName: String,
    @SerializedName("workspace_id")
    val workspaceId: Int,
    @SerializedName("board_description")
    val boardDescription: String? = null,
) : Parcelable