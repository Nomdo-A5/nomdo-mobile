package com.nomdoa5.nomdo.repository.model.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskRequest(
    @SerializedName("task_name")
    val taskName: String? = null,
    @SerializedName("task_description")
    val taskDescription: String? = null,
    @SerializedName("board_id")
    val boardId: Int? = null,
) : Parcelable
