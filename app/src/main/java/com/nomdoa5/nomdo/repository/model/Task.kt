package com.nomdoa5.nomdo.repository.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    @SerializedName("id")
    val id: Int,
    @SerializedName("task_name")
    val taskName: String? = null,
    @SerializedName("task_description")
    val taskDescription: String? = null,
    @SerializedName("due_date")
    val dueDate: String? = null,
    @SerializedName("is_done")
    val isDone: Int? = null,
    @SerializedName("is_finishedBy")
    val isFinishedBy: Int? = null,
) : Parcelable
