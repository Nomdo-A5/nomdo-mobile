package com.nomdoa5.nomdo.ui.workspaces

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.helpers.adapter.WorkspaceAdapter
import com.nomdoa5.nomdo.databinding.FragmentSharedWorkspacesBinding
import com.nomdoa5.nomdo.repository.model.Workspace
import com.nomdoa5.nomdo.ui.MainActivity

class SharedWorkspacesFragment : Fragment(), WorkspaceAdapter.OnWorkspaceClickListener {
    private var _binding: FragmentSharedWorkspacesBinding? = null
    private val binding get() = _binding!!
    private val workspaceAdapter = WorkspaceAdapter(this)
    private var workspaces = arrayListOf<Workspace>()
    private lateinit var workspaceName: Array<String>
    private lateinit var workspaceCreator: Array<String>
    private lateinit var rvWorkspace: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSharedWorkspacesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        (activity as MainActivity?)!!.setupToolbarMain()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
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
        rvWorkspace = requireView().findViewById(R.id.rv_shared_workspaces)
        rvWorkspace.setHasFixedSize(true)
        rvWorkspace.addItemDecoration(WorkspaceAdapter.MarginItemDecoration(15))
        rvWorkspace.layoutManager = LinearLayoutManager(context)
        workspaceAdapter.setData(workspaces)
        rvWorkspace.adapter = workspaceAdapter
    }

    override fun onWorkspaceClick(data: Workspace) {
        Snackbar.make(requireView(), "Kamu mengklik #${data.id}", Snackbar.LENGTH_SHORT).show()
        val action =
            SharedWorkspacesFragmentDirections.actionNavSharedWorkspacesToNavBoards(data.id.toString())
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onWorkspaceLongClick(data: Workspace) {
        Toast.makeText(requireContext(), "Longpress ${data.id}", Toast.LENGTH_SHORT).show()
    }
}