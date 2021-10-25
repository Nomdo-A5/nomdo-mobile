package com.nomdoa5.nomdo.ui.tasks

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
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.FragmentTaskBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.helpers.adapter.TaskAdapter
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Task
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.workspaces.SharedWorkspacesFragmentDirections

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class TaskFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var tasksViewModel: TasksViewModel
    private lateinit var authViewModel: AuthViewModel
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!
    private val taskAdapter = TaskAdapter()
    private var tasks = arrayListOf<Task>()
    private lateinit var taskName: Array<String>
    private lateinit var createdAt: Array<String>
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
        setData()
        setupViewModel()
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setData(){
        taskName = resources.getStringArray(R.array.name)
        createdAt = resources.getStringArray(R.array.creator)

        for(i in taskName.indices){
            val task = Task(
                i,
                taskName[i],
                "Owned by " + createdAt[i],
            )
            tasks.add(task)
        }
    }

    fun setupRecyclerView(){
        binding.swipeMyTask.isRefreshing = true
        rvTask = requireView().findViewById(R.id.rv_tasks)
        rvTask.setHasFixedSize(true)
        rvTask.addItemDecoration(TaskAdapter.MarginItemDecoration(15))
        rvTask.layoutManager = LinearLayoutManager(context)
//        taskAdapter.setData(tasks)
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            tasksViewModel.setTask(it!!, args.idBoard)
        })

        tasksViewModel.getTask().observe(viewLifecycleOwner, {
            taskAdapter.setData(it)
            binding.swipeMyTask.isRefreshing = false
        })
        rvTask.adapter = taskAdapter

        taskAdapter.setOnItemClickCallback(object : TaskAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Task) {
                val task = Task(data.idTask, data.taskName, data.taskDescription, data.boardId)
                val action = TaskFragmentDirections.actionNavTasksToDetailTaskFragment(task)
                Navigation.findNavController(requireView()).navigate(action)
            }
        })
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        tasksViewModel =
            ViewModelProvider(this).get(TasksViewModel::class.java)
    }

    override fun onRefresh() {
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            tasksViewModel.setTask(it!!, args.idBoard)
        })

        tasksViewModel.getSetTaskState().observe(viewLifecycleOwner, {
            if (it) {
                binding.swipeMyTask.isRefreshing = false
            }
        })
    }
}