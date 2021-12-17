package com.nomdoa5.nomdo.ui.workspace

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.FragmentProfileWorkspaceBinding
import com.nomdoa5.nomdo.helpers.MarginItemDecoration
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.helpers.adapter.ListViewMemberAdapter
import com.nomdoa5.nomdo.helpers.adapter.WorkspaceAdapter
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.User
import com.nomdoa5.nomdo.repository.model.Workspace
import com.nomdoa5.nomdo.ui.auth.AuthViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class ProfileWorkspaceFragment : Fragment(), ListViewMemberAdapter.OnMemberClickListener,
    View.OnClickListener {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var workspacesViewModel: WorkspacesViewModel
    private var _binding: FragmentProfileWorkspaceBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvMember: RecyclerView
    private var memberAdapter: ListViewMemberAdapter = ListViewMemberAdapter(this)
    private val args: ProfileWorkspaceFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileWorkspaceBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupDescription()
        setupRecyclerView()
        binding.layoutInviteMember.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setupDescription() {
        val description = requireActivity().findViewById<TextView>(R.id.tv_desc_profile_workspace)

        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            workspacesViewModel.setDetailWorkspace(it!!, args.workspace.id.toString())
        })

        description.text = args.workspace.workspaceDescription

    }

    private fun setupRecyclerView() {
        rvMember = requireView().findViewById(R.id.rv_member_profile_workspace)
        rvMember.setHasFixedSize(true)
        rvMember.addItemDecoration(MarginItemDecoration(16))
        rvMember.layoutManager = LinearLayoutManager(context)

        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            workspacesViewModel.setMemberWorkspace(it!!, args.workspace.id.toString())
        })

        workspacesViewModel.getMemberWorkspace().observe(viewLifecycleOwner, {
            memberAdapter.setData(it)
        })

        rvMember.adapter = memberAdapter
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        workspacesViewModel =
            ViewModelProvider(this).get(WorkspacesViewModel::class.java)
    }

    override fun onMemberClick(data: User) {
        val userProfileDialogFragment = UserProfileDialogFragment()
        val bundle = Bundle()
        bundle.putParcelable("EXTRA_USER", data)
        userProfileDialogFragment.arguments = bundle
        userProfileDialogFragment.show(
            requireActivity().supportFragmentManager,
            "Detail Member Dialog"
        )
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.layoutInviteMember -> {
                val shareWorkspaceFragment = ShareWorkspaceDialogFragment()
                val bundle = Bundle()
                bundle.putParcelable("EXTRA_WORKSPACE", args.workspace)
                shareWorkspaceFragment.arguments = bundle
                shareWorkspaceFragment.show(
                    requireActivity().supportFragmentManager,
                    "Share Workspace Dialog"
                )
            }
        }
    }
}