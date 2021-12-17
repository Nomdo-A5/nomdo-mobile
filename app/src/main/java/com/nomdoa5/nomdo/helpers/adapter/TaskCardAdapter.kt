package com.nomdoa5.nomdo.helpers.adapter

import android.content.res.Resources.getSystem
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.ItemTaskBinding
import com.nomdoa5.nomdo.databinding.ItemTaskCardBinding
import com.nomdoa5.nomdo.repository.model.Task

class TaskCardAdapter(private val listener: OnTaskClickListener) : RecyclerView.Adapter<TaskCardAdapter.TaskViewHolder>() {
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
            .inflate(R.layout.item_task_card, viewGroup, false)

        return TaskViewHolder(mView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int = mData.size

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemTaskCardBinding.bind(itemView)
        fun bind(taskItem: Task) {
            binding.tvTitleTask.text = taskItem.taskName
            binding.tvDateTask.text = taskItem.dueDate
            binding.cbTask.isChecked = taskItem.isDone!! > 0

            itemView.setOnClickListener { listener.onTaskClick(taskItem) }
            binding.cbTask.setOnClickListener{ listener.onCbTaskClick(taskItem) }
        }
    }
}