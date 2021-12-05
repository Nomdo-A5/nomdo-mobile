package com.nomdoa5.nomdo.repository.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Workspace(
    val id: Int,
    @SerializedName("workspace_name")
    val workspaceName: String? = null,
    @SerializedName("workspace_description")
    val workspaceDescription: String? = null,
    @SerializedName("url_join")
    val urlJoin: String? = null,
) : Parcelable