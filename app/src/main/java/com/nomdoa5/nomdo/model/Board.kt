package com.nomdoa5.nomdo.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

@Parcelize
data class Board(
    @SerializedName("id_board")
    val idBoard: Int,
    @SerializedName("board_name")
    val boardName: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("opened_at")
    val openedAt: String? = null,
    @SerializedName("id_workspace")
    val idWorkspace: Int?=null
) : Parcelable
