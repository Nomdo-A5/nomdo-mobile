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
import androidx.navigation.Navigation
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
    private var boardArgs: Board,
    private var type: Int?
) : Fragment(), SwipeRefreshLayout.OnRefreshListener,
    TaskAdapter.OnTaskClickListener {
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var authViewModel: AuthViewModel
    private var _binding: FragmentTaskPageBinding? = null
    private val binding get() = _binding!!
    private val taskAdapter = TaskAdapter(this)
    private lateinit var rvTask: RecyclerView

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
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setupRecyclerView() {
        binding.swipeMyTask.isRefreshing = true
        rvTask = requireView().findViewById(R.id.rv_tasks)
        rvTask.setHasFixedSize(true)
        rvTask.layoutManager = LinearLayoutManager(context)
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            taskViewModel.setTask(it!!, boardArgs.id.toString())
        })

        taskViewModel.getTask().observe(viewLifecycleOwner, {
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
            binding.imgEmptyTask.visibility = View.VISIBLE
            binding.tvEmptyTask.visibility = View.VISIBLE
        } else {
            binding.imgEmptyTask.visibility = View.GONE
            binding.tvEmptyTask.visibility = View.GONE
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
            taskViewModel.setTask(it!!, boardArgs.id.toString())
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
        val action = TaskFragmentDirections.actionNavTasksToDetailTaskFragment(data)
        Navigation.findNavController(requireView()).navigate(action)
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