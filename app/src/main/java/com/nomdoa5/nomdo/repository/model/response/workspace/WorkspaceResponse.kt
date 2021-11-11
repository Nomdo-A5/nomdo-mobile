package com.nomdoa5.nomdo.repository.model.response.workspace

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.nomdoa5.nomdo.repository.model.Workspace
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkspaceResponse(
    val workspace: ArrayList<Workspace>
) : Parcelable