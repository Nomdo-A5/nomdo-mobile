package com.nomdoa5.nomdo.helpers

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(spaceHeight: Int) : RecyclerView.ItemDecoration() {
    private val spaceHeightDp = spaceHeight.toDp()
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                top = spaceHeightDp
            }
            left = spaceHeightDp
            right = spaceHeightDp
            bottom = spaceHeightDp
        }
    }
}