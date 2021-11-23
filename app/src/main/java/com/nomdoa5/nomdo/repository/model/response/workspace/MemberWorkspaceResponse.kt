package com.nomdoa5.nomdo.repository.model.response.workspace

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.nomdoa5.nomdo.repository.model.User
import com.nomdoa5.nomdo.repository.model.Workspace
import kotlinx.parcelize.Parcelize

@Parcelize
data class MemberWorkspaceResponse(
    val member: ArrayList<User>,
    val message: String
) : Parcelable