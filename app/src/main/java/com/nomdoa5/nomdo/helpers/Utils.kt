package com.nomdoa5.nomdo.helpers

import android.content.ContentResolver
import android.content.res.Resources
import android.net.Uri
import android.provider.OpenableColumns
import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

fun Double.toCurrencyFormat(): String {
    val formatter = DecimalFormat("#,###")
    val formattedNumber = formatter.format(this)
    return "Rp. $formattedNumber"
}

fun View.snackbar(message: String) {
    Snackbar.make(
        this,
        message,
        Snackbar.LENGTH_LONG
    ).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }
    }.show()
}

fun ContentResolver.getFileName(fileUri: Uri): String {
    var name = ""
    val returnCursor = this.query(fileUri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return name
}

fun Int.toDp(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
