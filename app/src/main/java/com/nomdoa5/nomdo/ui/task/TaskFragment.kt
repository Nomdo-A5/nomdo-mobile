package com.nomdoa5.nomdo.ui.task

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.FragmentTaskBinding
import com.nomdoa5.nomdo.databinding.ItemTaskBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.helpers.adapter.TaskAdapter
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Task
import com.nomdoa5.nomdo.repository.model.request.task.UpdateTaskRequest
import com.nomdoa5.nomdo.ui.MainActivity
import com.nomdoa5.nomdo.ui.auth.AuthViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class TaskFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener,
    TaskAdapter.OnTaskClickListener {
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var authViewModel: AuthViewModel
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!
    private val taskAdapter = TaskAdapter(this)
    private lateinit var rvTask: RecyclerView
    private val args: TaskFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
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

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)!!.setupToolbarBoard(args.board.boardName!!, args.workspaceName)
    }

    fun setupRecyclerView() {
        binding.swipeMyTask.isRefreshing = true
        rvTask = requireView().findViewById(R.id.rv_tasks)
        rvTask.setHasFixedSize(true)
        rvTask.addItemDecoration(TaskAdapter.MarginItemDecoration(15))
        rvTask.layoutManager = LinearLayoutManager(context)
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            taskViewModel.setTask(it!!, args.board.id.toString())
        })

        taskViewModel.getTask().observe(viewLifecycleOwner, {
            taskAdapter.setData(it)
            binding.swipeMyTask.isRefreshing = false
        })
        rvTask.adapter = taskAdapter
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        taskViewModel =
            ViewModelProvider(this).get(TaskViewModel::class.java)
    }

    override fun onRefresh() {
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            taskViewModel.setTask(it!!, args.board.id.toString())
        })

        taskViewModel.getSetTaskState().observe(viewLifecycleOwner, {
            if (it == true) {
                binding.swipeMyTask.isRefreshing = false
            }
        })
    }

    override fun onTaskClick(data: Task) {
        val action = TaskFragmentDirections.actionNavTasksToDetailTaskFragment(data)
        Navigation.findNavController(requireView()).navigate(action)
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

        taskViewModel.getUpdateTaskState().observe(this, {
            if (it) {
                Toast.makeText(requireContext(), "Check Updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Error ya ges ya", Toast.LENGTH_SHORT).show()
            }
        })
    }
}