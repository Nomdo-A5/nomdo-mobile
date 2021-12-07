package com.nomdoa5.nomdo.ui.task

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.FragmentDetailTaskBinding
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.helpers.adapter.MemberAdapter
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.User
import com.nomdoa5.nomdo.repository.model.request.task.UpdateTaskRequest
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import kotlinx.coroutines.flow.collect

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class DetailTaskFragment : Fragment(), View.OnClickListener {
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var authViewModel: AuthViewModel
    private var _binding: FragmentDetailTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var name: Array<String>
    private val memberAdapter = MemberAdapter()
    private var members = arrayListOf<User>()
    private lateinit var rvMember: RecyclerView
    private val args: DetailTaskFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailTaskBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setData()
        setupViewModel()
        setupRecyclerView()
        setupDetailTask()

        binding.btnUpdateTask.setOnClickListener(this)
        binding.btnDeleteUpdateTask.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setData() {
        name = resources.getStringArray(R.array.name)
        for (i in name.indices) {
            val member = User(
                i,
                name[i]
            )
            members.add(member)
        }
    }

    fun setupRecyclerView() {
        rvMember = requireView().findViewById(R.id.rv_member)
        rvMember.setHasFixedSize(true)
        rvMember.addItemDecoration(MemberAdapter.MarginItemDecoration(5))
        val horizontalLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvMember.layoutManager = horizontalLayoutManager
        memberAdapter.setData(members)
        rvMember.adapter = memberAdapter

        memberAdapter.setOnItemClickCallback(object : MemberAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                Snackbar.make(
                    requireView(),
                    "Kamu mengklik #${data.name}",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        })
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        taskViewModel =
            ViewModelProvider(this).get(TaskViewModel::class.java)
    }

    fun setupDetailTask() {
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            taskViewModel.setDetailTask(it!!, args.task.id.toString())
        })

        binding.editNameUpdateTask.setText(args.task.taskName)
        binding.editDescUpdateTask.setText(args.task.taskDescription)
        binding.tvCalendar.setText(args.task.dueDate)
    }

    override fun onClick(v: View?) {
        when(v){
            binding.btnUpdateTask -> {
                binding.btnUpdateTask.startAnimation()
                val taskName = binding.editNameUpdateTask.text.toString()
                val taskDescription = binding.editDescUpdateTask.text.toString()
                val taskDueDate = binding.tvCalendar.text.toString()
                val task = UpdateTaskRequest(args.task.id, taskName, taskDescription, taskDueDate, args.task.isDone, args.task.isFinishedBy)

                authViewModel.getAuthToken().observe(viewLifecycleOwner, {
                    taskViewModel.updateTask(it!!, task)
                })

                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    taskViewModel.taskState.collect {
                        when (it) {
                            is LoadingState.Loading -> {
                                binding.btnUpdateTask.startAnimation()
                            }
                            is LoadingState.Success -> {
                                showSnackbar("Task Updated")
                                binding.btnUpdateTask.revertAnimation()
                            }
                            is LoadingState.Error -> {
                                binding.btnUpdateTask.revertAnimation()
                                showSnackbar(it.message)
                            }
                            else -> Unit
                        }
                    }
                }
            }
            binding.btnDeleteUpdateTask -> {
                authViewModel.getAuthToken().observe(viewLifecycleOwner, {
                    val idTask = args.task.id.toString()
                    taskViewModel.deleteTask(it!!, idTask)
                })

                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    taskViewModel.taskState.collect {
                        when (it) {
                            is LoadingState.Loading -> {
                                binding.btnUpdateTask.startAnimation()
                            }
                            is LoadingState.Success -> {
                                showSnackbar("Task Deleted")
                                binding.btnUpdateTask.revertAnimation()
                                Handler(Looper.getMainLooper()).postDelayed({
                                    parentFragmentManager.popBackStack()
                                }, 1000)
                            }
                            is LoadingState.Error -> {
                                binding.btnUpdateTask.revertAnimation()
                                showSnackbar(it.message)
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }
}