package com.nomdoa5.nomdo.repository.model.response.board

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.nomdoa5.nomdo.repository.model.Board
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateBoardResponse(
    val message: String,
    val board: Board,
) : Parcelable
