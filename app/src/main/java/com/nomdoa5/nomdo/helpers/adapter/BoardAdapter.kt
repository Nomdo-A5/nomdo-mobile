package com.nomdoa5.nomdo.helpers.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.ItemBoardBinding
import com.nomdoa5.nomdo.repository.model.Board
import com.nomdoa5.nomdo.repository.model.TaskInformation

class BoardAdapter(private val listener: OnBoardClickListener) :
    RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {
    private val mData = ArrayList<Board>()
    private val taskProgressData = ArrayList<TaskInformation>()

    fun setData(items: ArrayList<Board>?, progress: ArrayList<TaskInformation>?) {
        mData.clear()
        mData.addAll(items!!)
        taskProgressData.clear()
        taskProgressData.addAll(progress!!)
        notifyDataSetChanged()
    }

    interface OnBoardClickListener {
        fun onBoardClick(data: Board)
        fun onBoardLongClick(data: Board)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): BoardViewHolder {
        val mView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_board, viewGroup, false)

        return BoardViewHolder(mView, viewGroup.context)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.bind(mData[position], taskProgressData[position])
    }

    override fun getItemCount(): Int = mData.size

    inner class BoardViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemBoardBinding.bind(itemView)
        fun bind(boardItem: Board, progress: TaskInformation) {
            binding.tvTitleBoard.text = boardItem.boardName

            binding.tvCountBoard.text = progress.taskCount.toString()
            binding.tvDoneBoard.text = progress.taskDone.toString()

            val result: Double = if (progress.taskDone == 0) {
                0.0
            } else {
                (progress.taskDone!!.toDouble() / progress.taskCount!!) * 1000
            }

            var color : Int = ContextCompat.getColor(context, R.color.primary)
            if(result.toInt() < 500){
                color = ContextCompat.getColor(context, R.color.red)
            }else if(result.toInt() < 1000){
                color = ContextCompat.getColor(context, R.color.accent)
            }
            binding.progressBarBoard.progressTintList = ColorStateList.valueOf(color)
            binding.imgStatusProgress.imageTintList = ColorStateList.valueOf(color)

            binding.progressBarBoard.max = 1000
            binding.progressBarBoard.progress = result.toInt()

            itemView.setOnClickListener { listener.onBoardClick(boardItem) }
            itemView.setOnLongClickListener {
                listener.onBoardLongClick(boardItem)
                false
            }
        }
    }
}