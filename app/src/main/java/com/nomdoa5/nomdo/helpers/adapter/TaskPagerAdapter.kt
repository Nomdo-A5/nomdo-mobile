package com.nomdoa5.nomdo.helpers.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nomdoa5.nomdo.repository.model.Board
import com.nomdoa5.nomdo.ui.task.TaskPageFragment

class TaskPagerAdapter(fragmentActivity: Fragment) : FragmentStateAdapter(fragmentActivity) {
    private lateinit var args: Board

    fun setBoardArgs(board: Board){
        args = board
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null

        when (position) {
            0 -> fragment = TaskPageFragment(args, 0)
            1 -> fragment = TaskPageFragment(args, 1)
        }
        return fragment as Fragment
    }

    override fun getItemCount(): Int {
        return 2
    }
}