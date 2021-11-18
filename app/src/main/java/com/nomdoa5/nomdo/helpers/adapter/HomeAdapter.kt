package com.nomdoa5.nomdo.helpers.adapter

import android.content.res.Resources
import android.content.res.Resources.getSystem
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.ItemHomeBinding
import com.nomdoa5.nomdo.repository.model.Task

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {
    private val mData = ArrayList<Task>()
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setData(items: ArrayList<Task>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Task)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): HomeViewHolder {
        val mView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_home, viewGroup, false)

        return HomeViewHolder(mView)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int = mData.size

    inner class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemHomeBinding.bind(itemView)
        fun bind(homeItem: Task) {
            binding.tvTitleHome.text = homeItem.taskName

            itemView.setOnClickListener { onItemClickCallback?.onItemClicked(homeItem) }
        }
    }

    class MarginItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
        val spaceHeightDp = (spaceHeight * getSystem().displayMetrics.density).toInt()
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
}