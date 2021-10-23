package com.nomdoa5.nomdo.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.ItemBoardBinding
import com.nomdoa5.nomdo.model.Board

class BoardAdapter : RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {
    private val mData = ArrayList<Board>()
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setData(items: ArrayList<Board>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Board)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
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
            binding.tvNumBoard.text = boardItem.idBoard.toString()

            itemView.setOnClickListener { onItemClickCallback?.onItemClicked(boardItem) }
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