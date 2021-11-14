package com.nomdoa5.nomdo.repository.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskProgress(
    val taskCount: Int,
    val taskDoneCount: Int
) : Parcelable
