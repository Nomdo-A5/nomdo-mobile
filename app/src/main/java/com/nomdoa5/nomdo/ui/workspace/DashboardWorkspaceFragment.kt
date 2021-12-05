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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.FragmentDashboardWorkspaceBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.helpers.toCurrencyFormat
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Workspace
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.balance.BalanceViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class DashboardWorkspaceFragment : Fragment() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var workspacesViewModel: WorkspacesViewModel
    private lateinit var balanceViewModel: BalanceViewModel
    private var _binding: FragmentDashboardWorkspaceBinding? = null
    private val binding get() = _binding!!
    private var workspaces = arrayListOf<Workspace>()
    private lateinit var workspaceName: Array<String>
    private lateinit var workspaceCreator: Array<String>
    private lateinit var rvWorkspace: RecyclerView
    private val args: DashboardWorkspaceFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardWorkspaceBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupDashboard()
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
            )
            workspaces.add(workspace)
        }
    }

    fun setupDashboard() {
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            workspacesViewModel.setTaskInfo(it!!, args.workspace.id.toString())
            balanceViewModel.setOverviewBalance(it, args.workspace.id.toString())
        })

        workspacesViewModel.getTaskInfo().observe(viewLifecycleOwner, {
            binding.tvCompletedTask.text = it.taskDone.toString()
            binding.tvPendingTask.text = (it.taskCount!! - it.taskDone!!).toString()
        })

        balanceViewModel.getOverviewBalance().observe(viewLifecycleOwner, {
            binding.tvOutcomeBalanceOverview.text = it.outcomeBalance.toCurrencyFormat()
            binding.tvIncomeBalanceOverview.text = it.incomeBalance.toCurrencyFormat()
        })
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
        balanceViewModel = ViewModelProvider(this).get(BalanceViewModel::class.java)
    }
}