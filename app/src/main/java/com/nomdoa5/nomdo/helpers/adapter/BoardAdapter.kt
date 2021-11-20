package com.nomdoa5.nomdo.helpers.adapter

import android.content.res.Resources
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.ItemBoardBinding
import com.nomdoa5.nomdo.repository.model.Board
import com.nomdoa5.nomdo.repository.model.TaskProgress
import com.nomdoa5.nomdo.repository.model.Workspace

class BoardAdapter(private val listener: OnBoardClickListener) :
    RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {
    private val mData = ArrayList<Board>()
    private val taskProgressData = ArrayList<TaskProgress>()

    fun setData(items: ArrayList<Board>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    fun setTaskProgressData(items: ArrayList<TaskProgress>) {
        taskProgressData.clear()
        taskProgressData.addAll(items)
        notifyDataSetChanged()
    }

    interface OnBoardClickListener {
        fun onBoardClick(data: Board)
        fun onBoardLongClick(data: Board)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): BoardViewHolder {
        val mView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_board, viewGroup, false)

        return BoardViewHolder(mView)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int = mData.size

    inner class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemBoardBinding.bind(itemView)
        fun bind(boardItem: Board) {
            binding.tvTitleBoard.text = boardItem.boardName

            binding.tvNumBoard.text = boardItem.id.toString()
            val progress = boardItem.id.toString()
            binding.progress.progress = progress.toInt() * 10

            itemView.setOnClickListener { listener.onBoardClick(boardItem) }
            itemView.setOnLongClickListener {
                listener.onBoardLongClick(boardItem)
                false
            }
        }
    }

    class MarginItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
        val spaceHeightDp = (spaceHeight * Resources.getSystem().displayMetrics.density).toInt()
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