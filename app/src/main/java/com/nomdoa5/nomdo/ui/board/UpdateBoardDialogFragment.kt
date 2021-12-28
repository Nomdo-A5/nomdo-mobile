package com.nomdoa5.nomdo.ui.board

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.databinding.DialogFragmentUpdateBoardBinding
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.repository.model.Board
import com.nomdoa5.nomdo.repository.model.request.board.UpdateBoardRequest
import com.nomdoa5.nomdo.ui.MainActivity
import kotlinx.coroutines.flow.collect


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class UpdateBoardDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {
    private var _binding: DialogFragmentUpdateBoardBinding? = null
    private val binding get() = _binding!!
    private lateinit var boardsViewModel: BoardViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var board: Board
    private var listener: DismissListener? = null

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
            binding.btnUpdateBoard -> {
                val newBoardTitle = binding.editNameUpdateBoard.text.toString()
                val newBoard = UpdateBoardRequest(board.id, newBoardTitle, board.boardDescription.toString())

                authViewModel.getAuthToken().observe(this, {
                    boardsViewModel.updateBoard(it!!, newBoard)
                })

                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    boardsViewModel.boardState.collect {
                        when (it) {
                            is LoadingState.Loading -> {
                                binding.btnUpdateBoard.startAnimation()
                            }
                            is LoadingState.Success -> {
                                (activity as MainActivity?)!!.showSnackbar("Board Updated")
                                dismiss()
                            }
                            is LoadingState.Error -> {
                                binding.btnUpdateBoard.revertAnimation()
                                showSnackbar(it.message)
                            }
                            else -> Unit
                        }
                    }
                }

            }
            binding.btnDeleteBoard -> {
                binding.btnDeleteBoard.startAnimation()
                val idBoard = board.id.toString()
                authViewModel.getAuthToken().observe(this, {
                    boardsViewModel.deleteBoard(it!!, idBoard)
                })

                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    boardsViewModel.boardState.collect {
                        when (it) {
                            is LoadingState.Loading -> {
                                binding.btnDeleteBoard.startAnimation()
                            }
                            is LoadingState.Success -> {
                                (activity as MainActivity?)!!.showSnackbar("Board Deleted")
                                dismiss()
                            }
                            is LoadingState.Error -> {
                                binding.btnDeleteBoard.revertAnimation()
                                showSnackbar(it.message)
                            }
                            else -> Unit
                        }
                    }
                }
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
        boardsViewModel = ViewModelProvider(this).get(BoardViewModel::class.java)
    }


    private fun showSnackbar(message: String) {
        Snackbar.make(
            dialog!!.window!!.decorView, message, Snackbar.LENGTH_SHORT
        ).show()
    }
}