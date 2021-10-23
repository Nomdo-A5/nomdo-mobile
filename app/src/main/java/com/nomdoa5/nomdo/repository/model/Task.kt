package com.nomdoa5.nomdo.repository.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    @SerializedName("id_task")
    val idTask: Int,
    @SerializedName("task_name")
    val taskName: String? = null,
    @SerializedName("task_description")
    val taskDescription: String? = null,
    @SerializedName("board_id")
    val boardId: String? = null,
) : Parcelable
