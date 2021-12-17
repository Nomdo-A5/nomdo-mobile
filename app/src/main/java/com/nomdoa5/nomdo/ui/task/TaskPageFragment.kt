package com.nomdoa5.nomdo.ui.task

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.FragmentTaskPageBinding
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.helpers.adapter.TaskAdapter
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Board
import com.nomdoa5.nomdo.repository.model.Task
import com.nomdoa5.nomdo.repository.model.request.task.UpdateTaskRequest
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import kotlinx.coroutines.flow.collect

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class TaskPageFragment(
    private var boardArgs: Board
) : Fragment(), SwipeRefreshLayout.OnRefreshListener,
    TaskAdapter.OnTaskClickListener, View.OnClickListener {
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.dropdown_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.dropdown_close_anim
        )
    }
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var authViewModel: AuthViewModel
    private var _binding: FragmentTaskPageBinding? = null
    private val binding get() = _binding!!
    private val taskTodayAdapter = TaskAdapter(this)
    private val taskWeekAdapter = TaskAdapter(this)
    private val taskLaterAdapter = TaskAdapter(this)
    private val taskOverdueAdapter = TaskAdapter(this)
    private lateinit var rvTaskToday: RecyclerView
    private lateinit var rvTaskWeek: RecyclerView
    private lateinit var rvTaskLater: RecyclerView
    private lateinit var rvTaskOverdue: RecyclerView
    private var isClickedToday : Boolean = true
    private var isClickedWeek : Boolean = true
    private var isClickedLater : Boolean = true
    private var isClickedOverdue : Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskPageBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeMyTask.setOnRefreshListener(this)
        setupViewModel()
        setupRvToday()
        setupRvWeek()
        setupRvLater()
        setupRvOverdue()

        binding.cvTaskToday.setOnClickListener(this)
        binding.cvTaskWeek.setOnClickListener(this)
        binding.cvTaskLater.setOnClickListener(this)
        binding.cvTaskOverdue.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupCvNumber(dueDate: String, count: Int) {
        when(dueDate){
            "Today" -> binding.tvTitleTaskToday.text = "Do Today ($count)"
            "Week" -> binding.tvTitleTaskWeek.text = "Do Week ($count)"
            "Later" -> binding.tvTitleTaskLater.text = "Do Later ($count)"
            "Overdue" -> binding.tvTitleTaskOverdue.text = "Overdue ($count)"
        }
    }

    private fun setupRvToday() {
        binding.swipeMyTask.isRefreshing = true
        rvTaskToday = binding.rvTaskToday
        rvTaskToday.setHasFixedSize(true)
        rvTaskToday.layoutManager = LinearLayoutManager(context)
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            taskViewModel.setTask(it!!, boardArgs.id.toString(), 0, "Today")
        })

        taskViewModel.getTodayTask().observe(viewLifecycleOwner, {
            taskTodayAdapter.setData(it)
            setupCvNumber("Today", it.size)
            binding.swipeMyTask.isRefreshing = false
        })
        rvTaskToday.adapter = taskTodayAdapter
    }

    private fun setupRvWeek() {
        binding.swipeMyTask.isRefreshing = true
        rvTaskWeek = binding.rvTaskWeek
        rvTaskWeek.setHasFixedSize(true)
        rvTaskWeek.layoutManager = LinearLayoutManager(context)
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            taskViewModel.setTask(it!!, boardArgs.id.toString(), 0, "Week")
        })

        taskViewModel.getWeekTask().observe(viewLifecycleOwner, {
            taskWeekAdapter.setData(it)
            setupCvNumber("Week", it.size)
            binding.swipeMyTask.isRefreshing = false
        })
        rvTaskWeek.adapter = taskWeekAdapter
    }

    private fun setupRvLater() {
        binding.swipeMyTask.isRefreshing = true
        rvTaskLater = binding.rvTaskLater
        rvTaskLater.setHasFixedSize(true)
        rvTaskLater.layoutManager = LinearLayoutManager(context)
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            taskViewModel.setTask(it!!, boardArgs.id.toString(), 0, "Later")
        })

        taskViewModel.getLaterTask().observe(viewLifecycleOwner, {
            taskLaterAdapter.setData(it)
            setupCvNumber("Later", it.size)
            binding.swipeMyTask.isRefreshing = false
        })
        rvTaskLater.adapter = taskLaterAdapter
    }

    private fun setupRvOverdue() {
        binding.swipeMyTask.isRefreshing = true
        rvTaskOverdue = binding.rvTaskOverdue
        rvTaskOverdue.setHasFixedSize(true)
        rvTaskOverdue.layoutManager = LinearLayoutManager(context)
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            taskViewModel.setTask(it!!, boardArgs.id.toString(), 0, "Overdue")
        })

        taskViewModel.getOverdueTask().observe(viewLifecycleOwner, {
            taskOverdueAdapter.setData(it)
            setupCvNumber("Overdue", it.size)
            binding.swipeMyTask.isRefreshing = false
        })
        rvTaskOverdue.adapter = taskOverdueAdapter
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[AuthViewModel::class.java]
        taskViewModel =
            ViewModelProvider(this)[TaskViewModel::class.java]
    }

    override fun onRefresh() {
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            taskViewModel.setTask(it!!, boardArgs.id.toString(), 0, "Today")
            taskViewModel.setTask(it, boardArgs.id.toString(), 0, "Week")
            taskViewModel.setTask(it, boardArgs.id.toString(), 0, "Later")
            taskViewModel.setTask(it, boardArgs.id.toString(), 0, "Overdue")

        })

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            taskViewModel.taskState.collect {
                when (it) {
                    is LoadingState.Loading -> {
                        binding.swipeMyTask.isRefreshing = true
                    }
                    is LoadingState.Success -> {
                        binding.swipeMyTask.isRefreshing = false
                    }
                    is LoadingState.Error -> {
                        binding.swipeMyTask.isRefreshing = false
                        showSnackbar(it.message)
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onTaskClick(data: Task) {
        val addDialogFragment = UpdateTaskDialogFragment()
        val bundle = Bundle()
        bundle.putParcelable("EXTRA_TASK", data)
        addDialogFragment.arguments = bundle
        addDialogFragment.show(requireActivity().supportFragmentManager, "Update Task")
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onCbTaskClick(data: Task) {
        var check: Int? = data.isDone
        check = if (check!! > 0) {
            0
        } else {
            1
        }

        val task = UpdateTaskRequest(
            data.id,
            data.taskName,
            data.taskDescription,
            data.dueDate,
            check,
            data.isFinishedBy
        )

        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            taskViewModel.updateTask(it!!, task)
        })

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            taskViewModel.taskState.collect {
                when (it) {
                    is LoadingState.Success -> {
                        showSnackbar("Check Updated")
                    }
                    is LoadingState.Error -> {
                        showSnackbar(it.message)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showTodayRV(state: Boolean){
        if(state){
            binding.rvTaskToday.visibility = View.VISIBLE
            binding.imgTitleTaskToday.startAnimation(rotateOpen)
        }else{
            binding.rvTaskToday.visibility = View.GONE
            binding.imgTitleTaskToday.startAnimation(rotateClose)
        }
        isClickedToday = state
    }

    private fun showWeekRV(state: Boolean){
        if(state){
            binding.rvTaskWeek.visibility = View.VISIBLE
            binding.imgTitleTaskWeek.startAnimation(rotateOpen)
        }else{
            binding.rvTaskWeek.visibility = View.GONE
            binding.imgTitleTaskWeek.startAnimation(rotateClose)
        }
        isClickedWeek = state
    }

    private fun showLaterRV(state: Boolean){
        if(state){
            binding.rvTaskLater.visibility = View.VISIBLE
            binding.imgTitleTaskLater.startAnimation(rotateOpen)
        }else{
            binding.rvTaskLater.visibility = View.GONE
            binding.imgTitleTaskLater.startAnimation(rotateClose)
        }
        isClickedLater = state
    }

    private fun showOverdueRV(state: Boolean){
        if(state){
            binding.rvTaskOverdue.visibility = View.VISIBLE
            binding.imgTitleTaskOverdue.startAnimation(rotateOpen)
        }else{
            binding.rvTaskOverdue.visibility = View.GONE
            binding.imgTitleTaskOverdue.startAnimation(rotateClose)
        }
        isClickedOverdue = state
    }

    override fun onClick(v: View?) {
        when(v){
            binding.cvTaskToday -> showTodayRV(!isClickedToday)
            binding.cvTaskWeek -> showWeekRV(!isClickedWeek)
            binding.cvTaskLater -> showLaterRV(!isClickedLater)
            binding.cvTaskOverdue -> showOverdueRV(!isClickedOverdue)
        }
    }
}