package com.nomdoa5.nomdo.ui.task

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.nomdoa5.nomdo.databinding.FragmentTaskDonePageBinding
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

class TaskDonePageFragment(
    private var boardArgs: Board,
) : Fragment(), SwipeRefreshLayout.OnRefreshListener,
    TaskAdapter.OnTaskClickListener {
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var authViewModel: AuthViewModel
    private var _binding: FragmentTaskDonePageBinding? = null
    private val binding get() = _binding!!
    private val taskAdapter = TaskAdapter(this)
    private lateinit var rvTask: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDonePageBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeMyTask.setOnRefreshListener(this)
        setupViewModel()
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        binding.swipeMyTask.isRefreshing = true
        rvTask = binding.rvTaskDone
        rvTask.setHasFixedSize(true)
        rvTask.layoutManager = LinearLayoutManager(context)
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            taskViewModel.setTask(it!!, boardArgs.id.toString(), 1)
        })

        taskViewModel.getDoneTask().observe(viewLifecycleOwner, {
            taskAdapter.setData(it)
            binding.swipeMyTask.isRefreshing = false
            if (it.isEmpty()) {
                showEmpty(true)
            } else {
                showEmpty(false)
            }
        })
        rvTask.adapter = taskAdapter
    }

    private fun showEmpty(state: Boolean) {
        if (state) {
            binding.imgEmptyTaskDone.visibility = View.VISIBLE
            binding.tvEmptyTaskDone.visibility = View.VISIBLE
        } else {
            binding.imgEmptyTaskDone.visibility = View.GONE
            binding.tvEmptyTaskDone.visibility = View.GONE
        }
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
            taskViewModel.setTask(it!!, boardArgs.id.toString(), 1)
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
}