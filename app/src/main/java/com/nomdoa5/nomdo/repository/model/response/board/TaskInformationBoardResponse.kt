package com.nomdoa5.nomdo.repository.model.response.board

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.nomdoa5.nomdo.repository.model.User
import com.nomdoa5.nomdo.repository.model.Workspace
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskInformationBoardResponse(
    @SerializedName("task_count")
    val taskCount: Int?,
    @SerializedName("done_task")
    val doneTask: Int?,
    val message: String
) : Parcelable