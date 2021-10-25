package com.nomdoa5.nomdo.repository.model.request.workspace

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeleteWorkspaceRequest(
    val id : Int?,
) : Parcelable