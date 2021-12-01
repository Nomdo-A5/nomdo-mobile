package com.nomdoa5.nomdo.repository.model.response.workspace

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.nomdoa5.nomdo.repository.model.User
import com.nomdoa5.nomdo.repository.model.Workspace
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskInformationWorkspaceResponse(
    @SerializedName("task_count")
    val taskCount: Int?,
    @SerializedName("task_done")
    val taskDone: Int?,
    val message: String
) : Parcelable