package com.nomdoa5.nomdo.repository.model.response

import android.os.Parcelable
import com.nomdoa5.nomdo.repository.model.Balance
import com.nomdoa5.nomdo.repository.model.Board
import com.nomdoa5.nomdo.repository.model.Task
import com.nomdoa5.nomdo.repository.model.Workspace
import kotlinx.parcelize.Parcelize

@Parcelize
class SearchResponse(
    val workspace: ArrayList<Workspace>,
    val boards: ArrayList<Board>,
    val tasks: ArrayList<Task>,
    val balance: ArrayList<Balance>,
    var message: String,
) : Parcelable
