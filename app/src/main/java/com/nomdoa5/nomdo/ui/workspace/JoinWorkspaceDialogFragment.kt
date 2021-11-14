package com.nomdoa5.nomdo.ui.workspace

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.DialogFragmentCreateWorkspaceBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.request.ReportRequest
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.repository.model.request.workspace.WorkspaceRequest
import com.nomdoa5.nomdo.ui.balance.MoneyReportViewModel


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class JoinWorkspaceDialogBoard : DialogFragment(), View.OnClickListener {
    private var _binding: DialogFragmentCreateWorkspaceBinding? = null
    private val binding get() = _binding!!
    private lateinit var moneyReportViewModel: MoneyReportViewModel
    private lateinit var workspacesViewModel: WorkspacesViewModel
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentCreateWorkspaceBinding.inflate(inflater, container, false)
        val root: View = binding.root
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        binding.imgCloseAddWorkspace.setOnClickListener(this)
        binding.btnAddWorkspace.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnAddWorkspace -> {
                binding.btnAddWorkspace.startAnimation()
                val workspaceName =
                    binding.editNameAddWorkspace.text.toString()
                val workspace = WorkspaceRequest(workspaceName)
                authViewModel.getAuthToken().observe(this, { token ->
                    workspacesViewModel.addWorkspace(token!!, workspace)
                })

                authViewModel.getAuthToken().observe(this, { token ->
                    workspacesViewModel.getCreatedWorkspace().observe(this, {
                        val name = it.workspaceName + " Report"
                        val workspaceId = it.id
                        val moneyReport = ReportRequest(name, workspaceId)
                        Log.d("Workspace id", workspaceId.toString())
                        Log.d("Money report", moneyReport.toString())
                        moneyReportViewModel.addMoneyReport(token!!, moneyReport)
                    })
                })

                workspacesViewModel.getAddWorkspaceState().observe(this, {
                    if (it) {
                        Toast.makeText(requireContext(), "Workspace Added", Toast.LENGTH_SHORT)
                            .show()
                        binding.btnAddWorkspace.doneLoadingAnimation(
                            resources.getColor(R.color.teal_200),
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)!!
                                .toBitmap()
                        )
                        dismiss()
                    } else {
                        binding.btnAddWorkspace.revertAnimation()
                        Toast.makeText(
                            requireContext(),
                            "Add Workspace Failed!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
            binding.imgCloseAddWorkspace -> dismiss()
        }
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        workspacesViewModel = ViewModelProvider(this).get(WorkspacesViewModel::class.java)
        moneyReportViewModel = ViewModelProvider(this).get(MoneyReportViewModel::class.java)
    }
}
