package com.nomdoa5.nomdo.ui.task

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.databinding.DialogFragmentUpdateTaskBinding
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Task
import com.nomdoa5.nomdo.repository.model.request.task.UpdateTaskRequest
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import kotlinx.coroutines.flow.collect

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class UpdateTaskDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var authViewModel: AuthViewModel
    private var _binding: DialogFragmentUpdateTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var task: Task

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentUpdateTaskBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        task = requireArguments().getParcelable("EXTRA_TASK")!!
        setupViewModel()
        setupDetailTask()
        binding.btnUpdateTask.setOnClickListener(this)
        binding.btnDeleteTask.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[AuthViewModel::class.java]
        taskViewModel =
            ViewModelProvider(this)[TaskViewModel::class.java]
    }

    private fun setupDetailTask() {
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            taskViewModel.setDetailTask(it!!, task.id.toString())
        })

        binding.editNameUpdateTask.setText(task.taskName)
        binding.editDescUpdateTask.setText(task.taskDescription)
        binding.editDateUpdateTask.setText(task.dueDate)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnUpdateTask -> {
                binding.btnUpdateTask.startAnimation()
                val taskName = binding.editNameUpdateTask.text.toString()
                val taskDescription = binding.editDescUpdateTask.text.toString()
                val taskDueDate = binding.editDateUpdateTask.text.toString()
                val task = UpdateTaskRequest(
                    task.id,
                    taskName,
                    taskDescription,
                    taskDueDate,
                    task.isDone,
                    task.isFinishedBy
                )

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
            binding.btnDeleteTask -> {
                authViewModel.getAuthToken().observe(viewLifecycleOwner, {
                    val idTask = task.id.toString()
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
        Snackbar.make(
            dialog!!.window!!.decorView, message, Snackbar.LENGTH_SHORT
        ).show()
    }
}