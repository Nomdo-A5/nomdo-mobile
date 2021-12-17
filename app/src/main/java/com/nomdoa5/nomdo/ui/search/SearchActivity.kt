package com.nomdoa5.nomdo.ui.search

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.ActivitySearchBinding
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.helpers.MarginItemDecoration
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.helpers.adapter.*
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Balance
import com.nomdoa5.nomdo.repository.model.Board
import com.nomdoa5.nomdo.repository.model.Task
import com.nomdoa5.nomdo.repository.model.Workspace
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.balance.UpdateBalanceDialogFragment
import com.nomdoa5.nomdo.ui.board.BoardsFragmentDirections
import com.nomdoa5.nomdo.ui.board.UpdateBoardDialogFragment
import com.nomdoa5.nomdo.ui.task.TaskViewModel
import com.nomdoa5.nomdo.ui.workspace.UpdateWorkspaceDialogFragment
import kotlinx.coroutines.flow.collect

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class SearchActivity : AppCompatActivity(), View.OnClickListener, TextView.OnEditorActionListener,
    WorkspaceAdapter.OnWorkspaceClickListener, BoardAdapter.OnBoardClickListener,
    TaskAdapter.OnTaskClickListener, BalanceAdapter.OnBalanceClickListener {
    private var _binding: ActivitySearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var rvWorkspace: RecyclerView
    private lateinit var rvBoard: RecyclerView
    private lateinit var rvTask: RecyclerView
    private lateinit var rvBalance: RecyclerView
    private val adapterWorkspace = WorkspaceAdapter(this)
    private val adapterBoard = BoardAdapter(this)
    private val adapterTask = TaskAdapter(this)
    private val adapterBalance = BalanceAdapter(this, this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupRv()
        binding.include2.imgBackSearch.setOnClickListener(this)
        binding.include2.editSearch.setOnEditorActionListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.include2.imgBackSearch -> {
                finish()
            }
        }
    }

    override fun onEditorAction(p0: TextView?, action: Int, p2: KeyEvent?): Boolean {
        when (action) {
            EditorInfo.IME_ACTION_SEARCH -> {
                val query = binding.include2.editSearch.text.toString()
                search(query)
            }
        }
        return true
    }

    private fun search(query: String) {
        authViewModel.getAuthToken().observe(this, {
            searchViewModel.search(it!!, query)
        })

        lifecycleScope.launchWhenCreated {
            searchViewModel.loadingState.collect {
                when (it) {
                    is LoadingState.Loading -> {
                        showLoading(true)
                        showScrollLayout(false)
                    }
                    is LoadingState.Success -> {
                        showLoading(false)
                        showScrollLayout(true)
                    }
                    is LoadingState.Error -> {
                        showLoading(false)
                        showSnackbar(it.message)
                    }
                    else -> Unit
                }
            }
        }
        searchViewModel.searchResult.observe(this, {
            adapterWorkspace.setData(it.workspace)
//            adapterBoard.setData(it.boards)
            adapterTask.setData(it.tasks)
            adapterBalance.setData(it.balance)

            if (it.workspace.isEmpty() && it.boards.isEmpty() && it.tasks.isEmpty() && it.balance.isEmpty()) {
                showEmpty(true)
            } else {
                showEmpty(false)
            }
            if (it.workspace.isEmpty()) showWorkspace(false) else showWorkspace(
                true,
                it.workspace.size
            )
            if (it.boards.isEmpty()) showBoard(false) else showBoard(true, it.boards.size)
            if (it.tasks.isEmpty()) showTask(false) else showTask(true, it.tasks.size)
            if (it.balance.isEmpty()) showBalance(false) else showBalance(true, it.balance.size)
        })
    }

    private fun showScrollLayout(state: Boolean) {
        if (state) {
            binding.scrollviewSearch.visibility = View.VISIBLE
        } else {
            binding.scrollviewSearch.visibility = View.GONE
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun setupViewModel() {
        val pref = UserPreferences.getInstance(dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[AuthViewModel::class.java]
        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
    }

    private fun setupRv() {
        rvWorkspace = binding.rvWorkspaceSearch
        rvWorkspace.setHasFixedSize(true)
        rvWorkspace.addItemDecoration(MarginItemDecoration(16))
        rvWorkspace.layoutManager = LinearLayoutManager(this)
        rvWorkspace.adapter = adapterWorkspace

        rvBoard = binding.rvBoardSearch
        rvBoard.setHasFixedSize(true)
        rvBoard.addItemDecoration(MarginItemDecoration(16))
        rvBoard.layoutManager = LinearLayoutManager(this)
        rvBoard.adapter = adapterBoard

        rvTask = binding.rvTaskSearch
        rvTask.setHasFixedSize(true)
        rvTask.addItemDecoration(MarginItemDecoration(16))
        rvTask.layoutManager = LinearLayoutManager(this)
        rvTask.adapter = adapterTask

        rvBalance = binding.rvReportSearch
        rvBalance.setHasFixedSize(true)
        rvBalance.addItemDecoration(MarginItemDecoration(16))
        rvBalance.layoutManager = LinearLayoutManager(this)
        rvBalance.adapter = adapterBalance
    }


    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBarSearch.visibility = View.VISIBLE
        } else {
            binding.progressBarSearch.visibility = View.GONE
        }
    }

    private fun showWorkspace(state: Boolean, count: Int = 0) {
        if (state) {
            binding.tvWorkspaceSearch.text = "Workspace ($count)"
            binding.tvWorkspaceSearch.visibility = View.VISIBLE
            binding.rvWorkspaceSearch.visibility = View.VISIBLE
        } else {
            binding.tvWorkspaceSearch.visibility = View.GONE
            binding.rvWorkspaceSearch.visibility = View.GONE
        }
    }

    private fun showBoard(state: Boolean, count: Int = 0) {
        if (state) {
            binding.tvBoardSearch.text = "Board ($count)"
            binding.tvBoardSearch.visibility = View.VISIBLE
            binding.rvBoardSearch.visibility = View.VISIBLE
        } else {
            binding.tvBoardSearch.visibility = View.GONE
            binding.rvBoardSearch.visibility = View.GONE
        }
    }

    private fun showTask(state: Boolean, count: Int = 0) {
        if (state) {
            binding.tvTaskSearch.text = "Task ($count)"
            binding.tvTaskSearch.visibility = View.VISIBLE
            binding.rvTaskSearch.visibility = View.VISIBLE
        } else {
            binding.tvTaskSearch.visibility = View.GONE
            binding.rvTaskSearch.visibility = View.GONE
        }
    }

    private fun showBalance(state: Boolean, count: Int = 0) {
        if (state) {
            binding.tvMoneyReportSearch.text = "Money Report ($count)"
            binding.tvMoneyReportSearch.visibility = View.VISIBLE
            binding.rvReportSearch.visibility = View.VISIBLE
        } else {
            binding.tvMoneyReportSearch.visibility = View.GONE
            binding.rvReportSearch.visibility = View.GONE
        }
    }

    private fun showEmpty(state: Boolean) {
        if (state) {
            showScrollLayout(false)
            binding.imgEmptySearch.visibility = View.VISIBLE
            binding.tvEmptySearch.visibility = View.VISIBLE
        } else {
            showScrollLayout(true)
            binding.imgEmptySearch.visibility = View.GONE
            binding.tvEmptySearch.visibility = View.GONE
        }
    }

    override fun onWorkspaceClick(data: Workspace) {
        val addDialogFragment = UpdateWorkspaceDialogFragment()
        val bundle = Bundle()
        bundle.putParcelable("EXTRA_WORKSPACE", data)
        addDialogFragment.arguments = bundle
        addDialogFragment.show(supportFragmentManager, "Update Dialog")
    }

    override fun onWorkspaceLongClick(data: Workspace) {
        val addDialogFragment = UpdateWorkspaceDialogFragment()
        val bundle = Bundle()
        bundle.putParcelable("EXTRA_WORKSPACE", data)
        addDialogFragment.arguments = bundle
        addDialogFragment.show(supportFragmentManager, "Update Dialog")
    }

    override fun onBoardClick(data: Board) {
        val addBoardFragment = UpdateBoardDialogFragment()
        val bundle = Bundle()
        bundle.putParcelable("EXTRA_BOARD", data)
        addBoardFragment.arguments = bundle
        addBoardFragment.show(supportFragmentManager, "Update Dialog")
    }

    override fun onBoardLongClick(data: Board) {
        val addBoardFragment = UpdateBoardDialogFragment()
        val bundle = Bundle()
        bundle.putParcelable("EXTRA_BOARD", data)
        addBoardFragment.arguments = bundle
        addBoardFragment.show(supportFragmentManager, "Update Dialog")
    }

    override fun onTaskClick(data: Task) {

    }

    override fun onCbTaskClick(data: Task) {

    }

    override fun onBalanceClick(data: Balance) {
        val updateBalanceDialogFragment = UpdateBalanceDialogFragment()
        val bundle = Bundle()
        bundle.putParcelable("EXTRA_BALANCE", data)
        updateBalanceDialogFragment.arguments = bundle
        updateBalanceDialogFragment.show(supportFragmentManager, "Update Balance")
    }
}