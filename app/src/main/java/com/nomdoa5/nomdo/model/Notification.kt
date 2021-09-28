package com.nomdoa5.nomdo.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notification(
    @SerializedName("id_notification")
    val idNotification: Int,
    @SerializedName("notification_name")
    val notificationName: String? = null,
    val content: String? = null,
    val datetime: String? = null,
) : Parcelable
