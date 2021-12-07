package com.nomdoa5.nomdo.ui.task

import NoFilterAdapter
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.DialogFragmentCreateTaskBinding
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.repository.model.Board
import com.nomdoa5.nomdo.repository.model.request.task.TaskRequest
import com.nomdoa5.nomdo.ui.MainActivity
import com.nomdoa5.nomdo.ui.board.BoardViewModel
import com.nomdoa5.nomdo.ui.workspace.WorkspacesViewModel
import kotlinx.coroutines.flow.collect


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class CreateTaskDialogFragment : DialogFragment(), View.OnClickListener {
    private var _binding: DialogFragmentCreateTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var workspacesViewModel: WorkspacesViewModel
    private lateinit var boardsViewModel: BoardViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var board: Board
    private var spinnerWorkspacePosition: Int? = null
    private var spinnerBoardPosition: Int? = null
    private val workspaceAdapterId = ArrayList<String>()
    private val boardAdapterId = ArrayList<String>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentCreateTaskBinding.inflate(inflater, container, false)
        val root: View = binding.root
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupSpinner()
        binding.imgCloseAddTask.setOnClickListener(this)
        binding.btnAddTask.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnAddTask -> {
                val taskName = binding.editNameAddTask.text.toString()
                val taskDescription = binding.editDescAddTask.text.toString()
                val task = TaskRequest(taskName, taskDescription, spinnerBoardPosition)
                authViewModel.getAuthToken().observe(this, {
                    taskViewModel.addTask(it!!, task)
                })

                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    taskViewModel.taskState.collect {
                        when (it) {
                            is LoadingState.Loading -> {
                                binding.btnAddTask.startAnimation()
                            }
                            is LoadingState.Success -> {
                                (activity as MainActivity?)!!.showSnackbar("Task Created")
                                dismiss()
                            }
                            is LoadingState.Error -> {
                                binding.btnAddTask.revertAnimation()
                                showSnackbar(it.message)
                            }
                            else -> Unit
                        }
                    }
                }
            }
            binding.imgCloseAddTask -> {
                dismiss()
            }
        }
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        boardsViewModel = ViewModelProvider(this).get(BoardViewModel::class.java)
        workspacesViewModel = ViewModelProvider(this).get(WorkspacesViewModel::class.java)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun setupSpinner() {
        authViewModel.getAuthToken().observe(this, {
            workspacesViewModel.setWorkspace(it!!)
        })

        workspacesViewModel.getWorkspace().observe(this, {
            val workspaceName = ArrayList<String>()
            for (i in it) {
                workspaceName.add(i.workspaceName!!)
                workspaceAdapterId.add(i.id.toString())
            }
            val workspaceAdapter =
                NoFilterAdapter(
                    requireContext(),
                    R.layout.item_dropdown,
                    workspaceName.toArray(Array<String?>(workspaceName.size) { null })
                )
            binding.spinnerWorkspaceAddTask.setAdapter(workspaceAdapter)
        })

        binding.spinnerWorkspaceAddTask.setOnItemClickListener { _, _, position, _ ->
            spinnerWorkspacePosition = workspaceAdapterId[position].toInt()
            authViewModel.getAuthToken().observe(this@CreateTaskDialogFragment, {
                boardsViewModel.setBoard(it!!, spinnerWorkspacePosition.toString())
            })
            boardsViewModel.getBoard().observe(this@CreateTaskDialogFragment, {
                val boardName = ArrayList<String>()
                for (i in it) {
                    boardName.add(i.boardName!!)
                    boardAdapterId.add(i.id.toString())
                }
                val boardAdapter =
                    NoFilterAdapter(
                        requireContext(),
                        R.layout.item_dropdown,
                        boardName.toArray(Array<String?>(boardName.size) { null })
                    )
                binding.spinnerBoardAddTask.setAdapter(boardAdapter)
            })
        }

        binding.spinnerBoardAddTask.setOnItemClickListener { _, _, position, _ ->
            spinnerBoardPosition = boardAdapterId[position].toInt()
        }
    }
}