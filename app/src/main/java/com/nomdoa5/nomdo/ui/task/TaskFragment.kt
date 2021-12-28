package com.nomdoa5.nomdo.ui.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.nomdoa5.nomdo.databinding.FragmentTaskBinding
import com.nomdoa5.nomdo.helpers.adapter.TaskPagerAdapter
import com.nomdoa5.nomdo.ui.MainActivity
import android.view.MotionEvent
import android.view.View.OnTouchListener


class TaskFragment : Fragment() {
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!
    private val tabTitles = ArrayList<String>()
    val args: TaskFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)!!.apply {
            setupToolbarBoard(args.board.boardName!!, args.workspaceName)
            setupFabMargin(0)
        }
    }

    private fun setupViewPager() {
        tabTitles.add("To Do")
        tabTitles.add("Done")
        val pagerAdapter = TaskPagerAdapter(this)
        pagerAdapter.setBoardArgs(args.board)
        val viewPager: ViewPager2 = binding.viewPagerTask
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = 2
        val tabs: TabLayout = binding.tabsTask
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}