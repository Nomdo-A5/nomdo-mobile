package com.nomdoa5.nomdo.repository.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BoardInfo(
    val boards: ArrayList<Board>,
    val info: ArrayList<TaskInformation>
) : Parcelable
