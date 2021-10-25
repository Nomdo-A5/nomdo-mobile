package com.nomdoa5.nomdo.helpers.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.ItemWorkspaceBinding
import com.nomdoa5.nomdo.repository.model.Workspace

class WorkspaceAdapter(private val listener: OnWorkspaceClickListener) :
    RecyclerView.Adapter<WorkspaceAdapter.WorkspaceViewHolder>() {
    private val mData = ArrayList<Workspace>()

    fun setData(items: ArrayList<Workspace>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    interface OnWorkspaceClickListener {
        fun onWorkspaceClick(data: Workspace)
        fun onWorkspaceLongClick(data: Workspace)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): WorkspaceViewHolder {
        val mView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_workspace, viewGroup, false)

        return WorkspaceViewHolder(mView)
    }

    override fun onBindViewHolder(holder: WorkspaceViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int = mData.size

    inner class WorkspaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemWorkspaceBinding.bind(itemView)
        fun bind(workspaceItem: Workspace) {
            binding.tvTitleWorkspace.text = workspaceItem.workspaceName
            binding.tvCreator.text = workspaceItem.workspaceCreator

            itemView.setOnClickListener { listener.onWorkspaceClick(workspaceItem) }
            itemView.setOnLongClickListener {
                listener.onWorkspaceLongClick(workspaceItem)
                false
            }
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