package com.nomdoa5.nomdo.repository.model.request.workspace

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkspaceRequest(
    @SerializedName("workspace_name")
    val workspaceName: String? = null,
    @SerializedName("workspace_description")
    val workspaceDescription: String? = null,
) : Parcelable