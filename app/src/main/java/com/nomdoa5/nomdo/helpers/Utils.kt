package com.nomdoa5.nomdo.helpers

import java.text.DecimalFormat

fun Double.toCurrencyFormat(): String {
    val formatter = DecimalFormat("#,###")
    val formattedNumber = formatter.format(this)
    return "Rp. $formattedNumber"
}