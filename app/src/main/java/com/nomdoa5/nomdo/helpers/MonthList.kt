package com.nomdoa5.nomdo.helpers

object MonthList {
    private val month =
        arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Des")

    fun get(i: Int): String{
        return month[i]
    }
}