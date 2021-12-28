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
import com.nomdoa5.nomdo.helpers.*
import com.nomdoa5.nomdo.helpers.adapter.BoardAdapter
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Board
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.workspace.WorkspacesViewModel
import kotlinx.coroutines.flow.collect

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class BoardsFragment : Fragment(), BoardAdapter.OnBoardClickListener,
    SwipeRefreshLayout.OnRefreshListener, UpdateBoardDialogFragment.DismissListener {
    private lateinit var workspaceViewModel: WorkspacesViewModel
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        binding.swipeMyBoards.isRefreshing = true
        rvBoard = requireView().findViewById(R.id.rv_boards)
        rvBoard.setHasFixedSize(true)
        rvBoard.addItemDecoration(MarginItemDecoration(15))
        rvBoard.layoutManager = LinearLayoutManager(context)
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            boardViewModel.setBoard(it!!, args.workspace.id.toString())
            workspaceViewModel.setBoardInfo(it, args.workspace.id.toString())
        })

        val liveBoard = boardViewModel.getBoard()
        val liveBoardInfo = workspaceViewModel.getBoardInfo()
        val mergedBoardInfo = PairMediatorLiveData(liveBoard, liveBoardInfo)

        mergedBoardInfo.observe(viewLifecycleOwner, {
            if(it.first != null && it.second != null){
                if(it.first!!.size == it.second!!.taskInfo.size){
                    boardAdapter.setData(it.first, it.second?.taskInfo)
                }
            }
        })

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            boardViewModel.boardState.collect {
                when (it) {
                    is LoadingState.Loading -> {
                        binding.swipeMyBoards.isRefreshing = true
                    }
                    is LoadingState.Success -> {
                        binding.swipeMyBoards.isRefreshing = false
                        showEmpty(false)
                    }
                    is LoadingState.Error -> {
                        binding.swipeMyBoards.isRefreshing = false
                        showSnackbar(it.message)
                        showEmpty(true)
                    }
                    else -> Unit
                }
            }
        }

        rvBoard.adapter = boardAdapter
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[AuthViewModel::class.java]
        boardViewModel =
            ViewModelProvider(this)[BoardViewModel::class.java]
        workspaceViewModel = ViewModelProvider(this)[WorkspacesViewModel::class.java]
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
            BoardMenuFragmentDirections.actionBoardMenuFragmentToNavTasks(
                data,
                args.workspace.workspaceName!!
            )
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
            .navigate(action)
    }

    override fun onBoardLongClick(data: Board) {
        val addBoardFragment = UpdateBoardDialogFragment()
        val bundle = Bundle()
        bundle.putParcelable("EXTRA_BOARD", data)
        addBoardFragment.arguments = bundle
        addBoardFragment.showNow(requireActivity().supportFragmentManager, "Update Dialog")
        addBoardFragment.dialog!!.setCanceledOnTouchOutside(false)
        addBoardFragment.setDismissListener(this)
    }

    override fun onRefresh() {
        dispatchRefresh()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    fun dispatchRefresh() {
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            boardViewModel.setBoard(it!!, args.workspace.id.toString())
            workspaceViewModel.setBoardInfo(it, args.workspace.id.toString())
        })

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            boardViewModel.boardState.collect {
                when (it) {
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

    override fun onDismiss() {
        dispatchRefresh()
    }
}