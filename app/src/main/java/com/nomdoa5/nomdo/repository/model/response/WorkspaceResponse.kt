package com.nomdoa5.nomdo.repository.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkspaceResponse(
    @SerializedName("id_workspace")
    val idWorkspace: Int,
    @SerializedName("workspace_name")
    val workspaceName: String? = null,
) : Parcelable