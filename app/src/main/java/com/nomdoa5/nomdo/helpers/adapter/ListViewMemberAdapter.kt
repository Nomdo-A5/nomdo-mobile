package com.nomdoa5.nomdo.helpers.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.ItemMemberBinding
import com.nomdoa5.nomdo.databinding.ItemProfileWorkspaceMemberBinding
import com.nomdoa5.nomdo.repository.model.Task
import com.nomdoa5.nomdo.repository.model.User

class ListViewMemberAdapter(private val listener: ListViewMemberAdapter.OnMemberClickListener) :
    RecyclerView.Adapter<ListViewMemberAdapter.MemberViewHolder>() {
    private val mData = ArrayList<User>()

    fun setData(items: ArrayList<User>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    interface OnMemberClickListener {
        fun onMemberClick(data: User)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): MemberViewHolder {
        val mView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_profile_workspace_member, viewGroup, false)

        return MemberViewHolder(mView)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int = mData.size

    inner class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemProfileWorkspaceMemberBinding.bind(itemView)
        fun bind(userItem: User) {
            binding.imgAvatarMemberItem.setImageResource(R.drawable.ic_user)
            binding.tvNameItemMember.text = userItem.name
            itemView.setOnClickListener { listener.onMemberClick(userItem) }
        }
    }

    class MarginItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            with(outRect) {
                right = spaceHeight * 2
            }
        }
    }
}