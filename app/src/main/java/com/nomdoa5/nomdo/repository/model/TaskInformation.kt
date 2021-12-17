package com.nomdoa5.nomdo.repository.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskInformation(
    @SerializedName("task_count")
    val taskCount: Int?,
    @SerializedName("done_task")
    val taskDone: Int?,
) : Parcelable