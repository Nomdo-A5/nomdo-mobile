package com.nomdoa5.nomdo.ui.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.databinding.DialogFragmentUpdateBoardBinding
import com.nomdoa5.nomdo.repository.model.Board
import com.nomdoa5.nomdo.repository.model.request.DeleteRequest
import com.nomdoa5.nomdo.repository.model.request.board.UpdateBoardRequest
import com.nomdoa5.nomdo.ui.boards.BoardsViewModel


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class UpdateBoardDialogFragment : DialogFragment(), View.OnClickListener {
    private var _binding: DialogFragmentUpdateBoardBinding? = null
    private val binding get() = _binding!!
    private lateinit var boardsViewModel: BoardsViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var board: Board

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentUpdateBoardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        board = requireArguments().getParcelable("EXTRA_BOARD")!!
        binding.editNameUpdateBoard.setText(board.boardName)
        binding.btnUpdateBoard.setOnClickListener(this)
        binding.btnDeleteBoard.setOnClickListener(this)
        binding.imgCloseUpdateBoard.setOnClickListener(this)
        setupViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnUpdateBoard -> {
                binding.btnUpdateBoard.startAnimation()
                val newBoardTitle = binding.editNameUpdateBoard.text.toString()
                val newBoard = UpdateBoardRequest(board.id, newBoardTitle, board.boardDescription.toString())

                authViewModel.getAuthToken().observe(this, {
                    boardsViewModel.updateBoard(it!!, newBoard)
                })

                boardsViewModel.getUpdateBoardState()
                    .observe(this, object : Observer<Boolean?> {
                        override fun onChanged(isLoading: Boolean?) {
                            if (isLoading!!) {
                                Toast.makeText(
                                    requireContext(),
                                    "Update Board Success!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.btnUpdateBoard.doneLoadingAnimation(
                                    resources.getColor(R.color.teal_200),
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_check
                                    )!!
                                        .toBitmap()
                                )
                                dismiss()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Update Board Failed!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                binding.btnUpdateBoard.revertAnimation()
                            }
                        }
                    })

            }
            binding.btnDeleteBoard -> {
                binding.btnDeleteBoard.startAnimation()
                val idBoard = board.id.toString()
                authViewModel.getAuthToken().observe(this, {
                    boardsViewModel.deleteBoard(it!!, idBoard)
                })

                boardsViewModel.getDeleteBoardState().observe(this, {
                    if(it){
                        Toast.makeText(
                            requireContext(),
                            "Delete Workspace Success",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.btnDeleteBoard.doneLoadingAnimation(
                            resources.getColor(R.color.teal_200),
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_check
                            )!!
                                .toBitmap()
                        )
                        dismiss()
                    }else{
                        Toast.makeText(
                            requireContext(),
                            "Delete Workspace Failed!",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.btnDeleteBoard.revertAnimation()
                        dismiss()
                    }
                })
                dismiss()
            }
            binding.imgCloseUpdateBoard -> {
                dismiss()
            }
        }
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        boardsViewModel = ViewModelProvider(this).get(BoardsViewModel::class.java)
    }
}