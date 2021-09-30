package com.nomdoa5.nomdo.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Workspace(
    @SerializedName("id_workspace")
    val idWorkspace: Int,
    @SerializedName("workspace_name")
    val workspaceName: String? = null,
    val workspaceCreator: String? = null,
) : Parcelable
