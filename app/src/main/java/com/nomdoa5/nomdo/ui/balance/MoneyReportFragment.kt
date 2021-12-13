package com.nomdoa5.nomdo.ui.balance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.FragmentMoneyReportBinding
import com.nomdoa5.nomdo.helpers.adapter.MoneyReportPagerAdapter

class MoneyReportFragment : Fragment() {
    private var _binding: FragmentMoneyReportBinding? = null
    private val binding get() = _binding!!
    private val tabTitles = ArrayList<String>()
    val args: MoneyReportFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoneyReportBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        tabTitles.add("All")
        tabTitles.add("Planned")
        tabTitles.add("Done")
        val pagerAdapter = MoneyReportPagerAdapter(this)
        pagerAdapter.setWorkspaceArgs(args.workspace)
        val viewPager: ViewPager2 = requireView().findViewById(R.id.view_pager_money_report)
        viewPager.adapter = pagerAdapter
        val tabs: TabLayout = requireView().findViewById(R.id.tabs_money_report)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}