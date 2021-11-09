package com.nomdoa5.nomdo.helpers.adapter

import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.ItemTaskBinding
import com.nomdoa5.nomdo.repository.model.Board
import com.nomdoa5.nomdo.repository.model.Task

class TaskAdapter(private val listener: OnTaskClickListener) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private val mData = ArrayList<Task>()

    fun setData(items: ArrayList<Task>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    interface OnTaskClickListener {
        fun onTaskClick(data: Task)
        fun onCbTaskClick(data: Task)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): TaskViewHolder {
        val mView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_task, viewGroup, false)

        return TaskViewHolder(mView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int = mData.size

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemTaskBinding.bind(itemView)
        fun bind(taskItem: Task) {
            binding.tvTitleTask.text = taskItem.taskName
            binding.cbTask.isChecked = taskItem.isDone!! > 0

            itemView.setOnClickListener { listener.onTaskClick(taskItem) }
            binding.cbTask.setOnClickListener{ listener.onCbTaskClick(taskItem) }
        }
    }

    class MarginItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            with(outRect) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    top = spaceHeight * 2
                }
                left = spaceHeight * 2
                right = spaceHeight * 2
                bottom = spaceHeight
            }
        }
    }
}