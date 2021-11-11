package com.nomdoa5.nomdo.ui.board

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.nomdoa5.nomdo.databinding.FragmentBoardsBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.helpers.adapter.BoardAdapter
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Board
import com.nomdoa5.nomdo.ui.MainActivity
import com.nomdoa5.nomdo.ui.auth.AuthViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class BoardsFragment : Fragment(), BoardAdapter.OnBoardClickListener,
    SwipeRefreshLayout.OnRefreshListener {
    private lateinit var boardViewModel: BoardViewModel
    private lateinit var authViewModel: AuthViewModel
    private var _binding: FragmentBoardsBinding? = null
    private val binding get() = _binding!!
    private val boardAdapter = BoardAdapter(this)
    private var boards = arrayListOf<Board>()
    private lateinit var boardName: Array<String>
    private lateinit var createdAt: Array<String>
    private lateinit var rvBoard: RecyclerView
    private val args: BoardsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        (activity as MainActivity?)!!.setupToolbarWorkspace()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeMyBoards.setOnRefreshListener(this)
        setupViewModel()
        setData()
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setData() {
        boardName = resources.getStringArray(R.array.name)
        createdAt = resources.getStringArray(R.array.creator)


        for (i in boardName.indices) {
            val board = Board(
                i,
                boardName[i],
                "Owned by " + createdAt[i],
            )
            boards.add(board)
        }
    }

    fun setupRecyclerView() {
        binding.swipeMyBoards.isRefreshing = true
        rvBoard = requireView().findViewById(R.id.rv_boards)
        rvBoard.setHasFixedSize(true)
        rvBoard.addItemDecoration(BoardAdapter.MarginItemDecoration(15))
        rvBoard.layoutManager = LinearLayoutManager(context)
//        boardAdapter.setData(boards)
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            boardViewModel.setBoard(it!!, args.idWorkspace)
        })

        boardViewModel.getBoard().observe(viewLifecycleOwner, {
            boardAdapter.setData(it)
            binding.swipeMyBoards.isRefreshing = false
        })
        rvBoard.adapter = boardAdapter

    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        boardViewModel =
            ViewModelProvider(this).get(BoardViewModel::class.java)
    }

    override fun onBoardClick(data: Board) {
        Snackbar.make(
            requireView(),
            "Kamu mengklik #${data.id}",
            Snackbar.LENGTH_SHORT
        ).show()

        val action =
            BoardsFragmentDirections.actionNavBoardsToNavTasks(data.id.toString())
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onBoardLongClick(data: Board) {
        Toast.makeText(requireContext(), "Longpress ${data.id}", Toast.LENGTH_SHORT).show()
        val addBoardFragment = UpdateBoardDialogFragment()
        val bundle = Bundle()
        bundle.putParcelable("EXTRA_BOARD", data)
        addBoardFragment.arguments = bundle
        addBoardFragment.show(requireActivity().supportFragmentManager, "Update Dialog")
    }

    override fun onRefresh() {
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            boardViewModel.setBoard(it!!, args.idWorkspace)
        })

        boardViewModel.getSetBoardState().observe(viewLifecycleOwner, {
            if (it) {
                binding.swipeMyBoards.isRefreshing = false
            }
        })
    }
}