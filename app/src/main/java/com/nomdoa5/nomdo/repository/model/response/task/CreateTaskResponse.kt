package com.nomdoa5.nomdo.repository.model.response.task

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.nomdoa5.nomdo.repository.model.Task
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateTaskResponse(
    val task: Task
) : Parcelable
