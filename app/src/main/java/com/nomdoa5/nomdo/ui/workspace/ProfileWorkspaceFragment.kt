package com.nomdoa5.nomdo.ui.workspace

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.nomdoa5.nomdo.databinding.FragmentMyWorkspacesBinding
import com.nomdoa5.nomdo.databinding.FragmentProfileWorkspaceBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.helpers.adapter.WorkspaceAdapter
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Workspace
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.balance.MoneyReportFragmentArgs

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class ProfileWorkspaceFragment : Fragment() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var workspacesViewModel: WorkspacesViewModel
    private var _binding: FragmentProfileWorkspaceBinding? = null
    private val binding get() = _binding!!
    private var workspaces = arrayListOf<Workspace>()
    private lateinit var workspaceName: Array<String>
    private lateinit var workspaceCreator: Array<String>
    private lateinit var rvWorkspace: RecyclerView
    private val args: ProfileWorkspaceFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileWorkspaceBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupDescription()
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun setupDescription() {
        val description = requireActivity().findViewById<TextView>(R.id.tv_desc_profile_workspace)

        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            workspacesViewModel.setDetailWorkspace(it!!, args.workspace.id.toString())
        })

        description.text = args.workspace.workspaceDescription

    }

    fun setupRecyclerView() {
//        rvWorkspace = requireView().findViewById(R.id.rv_my_workspaces)
//        rvWorkspace.setHasFixedSize(true)
//        rvWorkspace.addItemDecoration(WorkspaceAdapter.MarginItemDecoration(16))
//        rvWorkspace.layoutManager = LinearLayoutManager(context)
//
//        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
//            workspacesViewModel.setWorkspace(it!!)
//        })
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        workspacesViewModel =
            ViewModelProvider(this).get(WorkspacesViewModel::class.java)
    }
}