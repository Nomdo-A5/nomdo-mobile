package com.nomdoa5.nomdo.repository.model.response.board

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.nomdoa5.nomdo.repository.model.TaskInformation
import com.nomdoa5.nomdo.repository.model.User
import com.nomdoa5.nomdo.repository.model.Workspace
import com.nomdoa5.nomdo.repository.model.response.workspace.TaskInformationWorkspaceResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskInformationBoardResponse(
    @SerializedName("task_info")
    val taskInfo: ArrayList<TaskInformation>
) : Parcelable