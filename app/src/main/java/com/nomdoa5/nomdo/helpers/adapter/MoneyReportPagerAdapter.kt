package com.nomdoa5.nomdo.helpers.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nomdoa5.nomdo.repository.model.Workspace
import com.nomdoa5.nomdo.ui.balance.MoneyReportPageFragment

class MoneyReportPagerAdapter(fragmentActivity: Fragment) : FragmentStateAdapter(fragmentActivity) {
    private lateinit var args: Workspace

    fun setWorkspaceArgs(workspace: Workspace){
        args = workspace
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null

        when (position) {
            0 -> fragment = MoneyReportPageFragment(args, 0)
            1 -> fragment = MoneyReportPageFragment(args, 1)
            2 -> fragment = MoneyReportPageFragment(args, 2)
        }
        return fragment as Fragment
    }

    override fun getItemCount(): Int {
        return 3
    }
}