package com.nomdoa5.nomdo.ui.workspaces

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.helpers.adapter.WorkspaceAdapter
import com.nomdoa5.nomdo.databinding.FragmentMyWorkspacesBinding
import com.nomdoa5.nomdo.repository.model.Workspace

class MyWorkspacesFragment : Fragment() {
//    private lateinit var myWorkspacesViewModel: MyWorkspacesViewModel
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
//        myWorkspacesViewModel =
//            ViewModelProvider(this).get(MyWorkspacesViewModel::class.java)

        _binding = FragmentMyWorkspacesBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        myWorkspacesViewModel.text.observe(viewLifecycleOwner, {
//            textView.text = it
//        })
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

    fun setData(){
        workspaceName = resources.getStringArray(R.array.name)
        workspaceCreator = resources.getStringArray(R.array.creator)

        for(i in workspaceName.indices){
            val workspace = Workspace(
                i,
                workspaceName[i],
                "Owned by " + workspaceCreator[i],
            )
            workspaces.add(workspace)
        }
    }

    fun setupRecyclerView(){
        rvWorkspace = requireView().findViewById(R.id.rv_my_workspaces)
        rvWorkspace.setHasFixedSize(true)
        rvWorkspace.addItemDecoration(WorkspaceAdapter.MarginItemDecoration(15))
        rvWorkspace.layoutManager = LinearLayoutManager(context)
        workspaceAdapter.setData(workspaces)
        rvWorkspace.adapter = workspaceAdapter

        workspaceAdapter.setOnItemClickCallback(object : WorkspaceAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Workspace) {
                Snackbar.make(requireView(), "Kamu mengklik #${data.idWorkspace}", Snackbar.LENGTH_SHORT).show()
                Navigation.findNavController(requireView()).navigate(R.id.action_nav_my_workspaces_to_nav_boards)
            }
        })
    }
}