package com.nomdoa5.nomdo.ui.dialog

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.DialogFragmentCreateBoardBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.databinding.DialogFragmentUpdateBoardBinding
import com.nomdoa5.nomdo.repository.model.Board
import com.nomdoa5.nomdo.repository.model.request.DeleteRequest
import com.nomdoa5.nomdo.repository.model.request.board.BoardRequest
import com.nomdoa5.nomdo.repository.model.request.board.UpdateBoardRequest
import com.nomdoa5.nomdo.repository.model.request.workspace.WorkspaceRequest
import com.nomdoa5.nomdo.ui.boards.BoardsViewModel
import com.nomdoa5.nomdo.ui.workspaces.WorkspacesViewModel


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class CreateBoardDialogFragment : DialogFragment(), View.OnClickListener {
    private var _binding: DialogFragmentCreateBoardBinding? = null
    private val binding get() = _binding!!
    private lateinit var workspacesViewModel: WorkspacesViewModel
    private lateinit var boardsViewModel: BoardsViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var board: BoardRequest
    private var spinnerWorkspacePosition: Int? = null
    private val workspaceAdapterId = ArrayList<String>()

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

    override fun onClick(v: View?) {
        when (v) {
            binding.btnAddBoard -> {
                binding.btnAddBoard.startAnimation()
                val boardName = binding.editNameAddBoard.text.toString()

                board = BoardRequest(boardName, spinnerWorkspacePosition!!)
                authViewModel.getAuthToken().observe(this, {
                    boardsViewModel.addBoard(it!!, board)
                })

                boardsViewModel.getAddBoardState().observe(this, {
                    if (it) {
                        Toast.makeText(requireContext(), "Board Added", Toast.LENGTH_SHORT).show()
                        binding.btnAddBoard.doneLoadingAnimation(
                            resources.getColor(R.color.teal_200),
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)!!
                                .toBitmap()
                        )
                        Handler(Looper.getMainLooper()).postDelayed({
                            dismiss()
                        }, 1000)
                    } else {
                        binding.btnAddBoard.revertAnimation()
                        Toast.makeText(requireContext(), "Add Board Failed!!", Toast.LENGTH_SHORT).show()
                    }
                })

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
        boardsViewModel = ViewModelProvider(this).get(BoardsViewModel::class.java)
        workspacesViewModel = ViewModelProvider(this).get(WorkspacesViewModel::class.java)
    }

    fun setupSpinner() {
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

        binding.spinnerWorkspaceAddBoard.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                spinnerWorkspacePosition = workspaceAdapterId[position].toInt()
            }
        })
    }
}