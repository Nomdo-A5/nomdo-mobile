package com.nomdoa5.nomdo.ui.board

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.FragmentBoardsBinding
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.helpers.adapter.BoardAdapter
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Board
import com.nomdoa5.nomdo.ui.MainActivity
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.task.TaskViewModel
import kotlinx.coroutines.flow.collect

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class BoardsFragment : Fragment(), BoardAdapter.OnBoardClickListener,
    SwipeRefreshLayout.OnRefreshListener {
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var boardViewModel: BoardViewModel
    private lateinit var authViewModel: AuthViewModel
    private var _binding: FragmentBoardsBinding? = null
    private val binding get() = _binding!!
    private val boardAdapter = BoardAdapter(this)
    private lateinit var rvBoard: RecyclerView
    private val args: BoardsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeMyBoards.setOnRefreshListener(this)
        setupViewModel()
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)!!.setupToolbarWorkspace(
            args.workspace.workspaceName!!,
            args.workspace
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setupRecyclerView() {
        binding.swipeMyBoards.isRefreshing = true
        rvBoard = requireView().findViewById(R.id.rv_boards)
        rvBoard.setHasFixedSize(true)
        rvBoard.addItemDecoration(BoardAdapter.MarginItemDecoration(15))
        rvBoard.layoutManager = LinearLayoutManager(context)
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            boardViewModel.setBoard(it!!, args.workspace.id.toString())
        })

        boardViewModel.getBoard().observe(viewLifecycleOwner, {
            boardAdapter.setData(it)
            binding.swipeMyBoards.isRefreshing = false
            if (it.isEmpty()) {
                showEmpty(true)
            } else {
                showEmpty(false)
            }
        })


        rvBoard.adapter = boardAdapter
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        boardViewModel =
            ViewModelProvider(this).get(BoardViewModel::class.java)
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
    }

    private fun showEmpty(state: Boolean) {
        if (state) {
            binding.imgEmptyBoard.visibility = View.VISIBLE
            binding.tvEmptyBoard.visibility = View.VISIBLE
        } else {
            binding.imgEmptyBoard.visibility = View.GONE
            binding.tvEmptyBoard.visibility = View.GONE
        }
    }

    override fun onBoardClick(data: Board) {
        val action =
            BoardsFragmentDirections.actionNavBoardsToNavTasks(data, args.workspace.workspaceName!!)
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onBoardLongClick(data: Board) {
        val addBoardFragment = UpdateBoardDialogFragment()
        val bundle = Bundle()
        bundle.putParcelable("EXTRA_BOARD", data)
        addBoardFragment.arguments = bundle
        addBoardFragment.show(requireActivity().supportFragmentManager, "Update Dialog")
    }

    override fun onRefresh() {
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            boardViewModel.setBoard(it!!, args.workspace.id.toString())
        })

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            boardViewModel.boardState.collect {
                when(it){
                    is LoadingState.Loading -> {
                        binding.swipeMyBoards.isRefreshing = true
                    }
                    is LoadingState.Success -> {
                        binding.swipeMyBoards.isRefreshing = false
                    }
                    is LoadingState.Error -> {
                        binding.swipeMyBoards.isRefreshing = false
                        showSnackbar(it.message)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }
}