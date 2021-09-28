package com.nomdoa5.nomdo.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(
    @SerializedName("id_article")
    val idArticle: Int,
    val title: String? = null,
    val description: String? = null,
    val date: String? = null,
) : Parcelable
