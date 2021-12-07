package com.nomdoa5.nomdo.ui.workspace

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.FragmentMyWorkspacesBinding
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.helpers.adapter.WorkspaceAdapter
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Workspace
import com.nomdoa5.nomdo.ui.MainActivity
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import kotlinx.coroutines.flow.collect

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class MyWorkspacesFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener,
    WorkspaceAdapter.OnWorkspaceClickListener {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var workspacesViewModel: WorkspacesViewModel
    private var _binding: FragmentMyWorkspacesBinding? = null
    private val binding get() = _binding!!
    private val workspaceAdapter = WorkspaceAdapter(this)
    private lateinit var rvWorkspace: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyWorkspacesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeMyWorkspaces.setOnRefreshListener(this)
        setupViewModel()
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)!!.setupToolbarMain("Workspaces")
    }

    private fun setupRecyclerView() {
        binding.swipeMyWorkspaces.isRefreshing = true
        rvWorkspace = requireView().findViewById(R.id.rv_my_workspaces)
        rvWorkspace.setHasFixedSize(true)
        rvWorkspace.addItemDecoration(WorkspaceAdapter.MarginItemDecoration(16))
        rvWorkspace.layoutManager = LinearLayoutManager(context)

        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            workspacesViewModel.setWorkspace(it!!)
        })

        workspacesViewModel.getWorkspace().observe(viewLifecycleOwner, {
            workspaceAdapter.setData(it)
            binding.swipeMyWorkspaces.isRefreshing = false
            if(it.isEmpty()){
                showEmpty(true)
            }else{
                showEmpty(false)
            }
        })
        rvWorkspace.adapter = workspaceAdapter
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        workspacesViewModel =
            ViewModelProvider(this).get(WorkspacesViewModel::class.java)
    }

    private fun showEmpty(state: Boolean) {
        if (state) {
            binding.imgEmptyWorkspace.visibility = View.VISIBLE
            binding.tvEmptyWorkspace.visibility = View.VISIBLE
        } else {
            binding.imgEmptyWorkspace.visibility = View.GONE
            binding.tvEmptyWorkspace.visibility = View.GONE
        }
    }

    override fun onRefresh() {
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            workspacesViewModel.setWorkspace(it!!)
        })

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            workspacesViewModel.workspaceState.collect {
                when (it) {
                    is LoadingState.Loading -> {
                        binding.swipeMyWorkspaces.isRefreshing = true
                    }
                    is LoadingState.Success -> {
                        binding.swipeMyWorkspaces.isRefreshing = false
                    }
                    is LoadingState.Error -> {
                        binding.swipeMyWorkspaces.isRefreshing = false
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onWorkspaceClick(data: Workspace) {
        val action =
            MyWorkspacesFragmentDirections.actionNavMyWorkspacesToNavBoards(data)
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onWorkspaceLongClick(data: Workspace) {
        val addDialogFragment = UpdateWorkspaceDialogFragment()
        val bundle = Bundle()
        bundle.putParcelable("EXTRA_WORKSPACE", data)
        addDialogFragment.arguments = bundle
        addDialogFragment.show(requireActivity().supportFragmentManager, "Update Dialog")
    }
}