package com.nomdoa5.nomdo.ui.workspaces

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
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.helpers.adapter.WorkspaceAdapter
import com.nomdoa5.nomdo.databinding.FragmentMyWorkspacesBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Workspace
import com.nomdoa5.nomdo.ui.auth.AuthViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class MyWorkspacesFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var myWorkspacesViewModel: MyWorkspacesViewModel
    private var _binding: FragmentMyWorkspacesBinding? = null
    private val binding get() = _binding!!
    private val workspaceAdapter = WorkspaceAdapter()
    private var workspaces = arrayListOf<Workspace>()
    private lateinit var workspaceName: Array<String>
    private lateinit var workspaceCreator: Array<String>
    private lateinit var rvWorkspace: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMyWorkspacesBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
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

    fun setData() {
        workspaceName = resources.getStringArray(R.array.name)
        workspaceCreator = resources.getStringArray(R.array.creator)

        for (i in workspaceName.indices) {
            val workspace = Workspace(
                i,
                workspaceName[i],
                "Owned by " + workspaceCreator[i],
            )
            workspaces.add(workspace)
        }
    }

    fun setupRecyclerView() {
        binding.swipeMyWorkspaces.isRefreshing = true
        rvWorkspace = requireView().findViewById(R.id.rv_my_workspaces)
        rvWorkspace.setHasFixedSize(true)
        rvWorkspace.addItemDecoration(WorkspaceAdapter.MarginItemDecoration(15))
        rvWorkspace.layoutManager = LinearLayoutManager(context)

        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            myWorkspacesViewModel.setWorkspace(it!!)
        })

        myWorkspacesViewModel.getWorkspace().observe(viewLifecycleOwner, {
            workspaceAdapter.setData(it)
            binding.swipeMyWorkspaces.isRefreshing = false
        })
        rvWorkspace.adapter = workspaceAdapter

        workspaceAdapter.setOnItemClickCallback(object : WorkspaceAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Workspace) {
                Snackbar.make(
                    requireView(),
                    "Kamu mengklik #${data.id}",
                    Snackbar.LENGTH_SHORT
                ).show()

                val action = MyWorkspacesFragmentDirections.actionNavMyWorkspacesToNavBoards(data.id.toString())
                Navigation.findNavController(requireView()).navigate(action)
            }
        })
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        myWorkspacesViewModel =
            ViewModelProvider(this).get(MyWorkspacesViewModel::class.java)
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBarMyWorkspaces.visibility = View.VISIBLE
        } else {
            binding.progressBarMyWorkspaces.visibility = View.GONE
        }
    }

    override fun onRefresh() {
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            myWorkspacesViewModel.setWorkspace(it!!)
        })

        myWorkspacesViewModel.getWorkspaceState().observe(viewLifecycleOwner, {
            if(it){
                binding.swipeMyWorkspaces.isRefreshing = false
            }
        })
    }
}