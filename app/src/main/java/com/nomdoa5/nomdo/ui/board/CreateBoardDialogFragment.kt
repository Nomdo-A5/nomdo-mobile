package com.nomdoa5.nomdo.ui.board

import NoFilterAdapter
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.DialogFragmentCreateBoardBinding
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.request.board.BoardRequest
import com.nomdoa5.nomdo.ui.MainActivity
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.workspace.WorkspacesViewModel
import kotlinx.coroutines.flow.collect


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class CreateBoardDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {
    private var _binding: DialogFragmentCreateBoardBinding? = null
    private val binding get() = _binding!!
    private lateinit var workspacesViewModel: WorkspacesViewModel
    private lateinit var boardsViewModel: BoardViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var board: BoardRequest
    private var spinnerWorkspacePosition: Int? = null
    private val workspaceAdapterId = ArrayList<String>()
    private var listener: DismissListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentCreateBoardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupSpinner()
        binding.imgCloseAddBoard.setOnClickListener(this)
        binding.btnAddBoard.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    interface DismissListener {
        fun onDismiss()
    }

    fun setDismissListener(listener: DismissListener) {
        this.listener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onDismiss()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnAddBoard -> {
                val boardName = binding.editNameAddBoard.text.toString()

                if (boardName.isEmpty()) {
                    binding.editNameAddBoard.error = "Must Enter"
                }

                if (binding.spinnerWorkspaceAddBoard.text.isNullOrEmpty()) {
                    binding.spinnerWorkspaceAddBoard.error = "Must Enter"
                }

                if (!(boardName.isEmpty() && binding.spinnerWorkspaceAddBoard.text.isNullOrEmpty())) {
                    board = BoardRequest(boardName, spinnerWorkspacePosition!!)
                    authViewModel.getAuthToken().observe(this, {
                        boardsViewModel.addBoard(it!!, board)
                    })

                    viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                        boardsViewModel.boardState.collect {
                            when (it) {
                                is LoadingState.Loading -> {
                                    binding.btnAddBoard.startAnimation()
                                }
                                is LoadingState.Success -> {
                                    (activity as MainActivity?)!!.showSnackbar("Board Created")
                                    dismiss()
                                }
                                is LoadingState.Error -> {
                                    binding.btnAddBoard.revertAnimation()
                                    showSnackbar(it.message)
                                }
                                else -> Unit
                            }
                        }
                    }
                }
            }
            binding.imgCloseAddBoard -> {
                dismiss()
            }
        }
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        boardsViewModel = ViewModelProvider(this).get(BoardViewModel::class.java)
        workspacesViewModel = ViewModelProvider(this).get(WorkspacesViewModel::class.java)
    }

    private fun setupSpinner() {
        binding.spinnerWorkspaceAddBoard
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
            binding.spinnerWorkspaceAddBoard.setAdapter(workspaceAdapter)
        })

        binding.spinnerWorkspaceAddBoard.setOnItemClickListener { _, _, position, _ ->
            spinnerWorkspacePosition = workspaceAdapterId[position].toInt()
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(
            dialog!!.window!!.decorView, message, Snackbar.LENGTH_SHORT
        ).show()
    }
}