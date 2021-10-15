package com.nomdoa5.nomdo.helpers.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.ItemMemberBinding
import com.nomdoa5.nomdo.repository.model.User

class MemberAdapter : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {
    private val mData = ArrayList<User>()
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setData(items: ArrayList<User>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: User)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): MemberViewHolder {
        val mView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_member, viewGroup, false)

        return MemberViewHolder(mView)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int = mData.size

    inner class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemMemberBinding.bind(itemView)
        fun bind(userItem: User) {
            binding.imgMemberItem.setImageResource(R.drawable.zaid)
            itemView.setOnClickListener { onItemClickCallback?.onItemClicked(userItem) }
        }
    }

    class MarginItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View,
                                    parent: RecyclerView, state: RecyclerView.State) {
            with(outRect) {
                right = spaceHeight * 2
            }
        }
    }
}