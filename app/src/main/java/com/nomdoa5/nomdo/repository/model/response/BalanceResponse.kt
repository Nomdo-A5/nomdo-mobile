package com.nomdoa5.nomdo.repository.model.response

import android.os.Parcelable
import com.nomdoa5.nomdo.repository.model.Balance
import kotlinx.parcelize.Parcelize

@Parcelize
data class BalanceResponse(
    val balance: ArrayList<Balance>
) : Parcelable