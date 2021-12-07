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
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.DialogFragmentCreateWorkspaceBinding
import com.nomdoa5.nomdo.databinding.DialogFragmentJoinWorkspaceBinding
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.request.ReportRequest
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.repository.model.request.workspace.WorkspaceRequest
import com.nomdoa5.nomdo.ui.balance.MoneyReportViewModel
import kotlinx.coroutines.flow.collect


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class JoinWorkspaceDialogBoard : DialogFragment(), View.OnClickListener {
    private var _binding: DialogFragmentJoinWorkspaceBinding? = null
    private val binding get() = _binding!!
    private lateinit var moneyReportViewModel: MoneyReportViewModel
    private lateinit var workspacesViewModel: WorkspacesViewModel
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentJoinWorkspaceBinding.inflate(inflater, container, false)
        val root: View = binding.root
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        binding.imgCloseJoinWorkspace.setOnClickListener(this)
        binding.btnJoinWorkspace.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnJoinWorkspace -> {
                val urlJoin = binding.editLinkJoinWorkspace.text.toString()
                if(urlJoin.isBlank()){
                    binding.editLinkJoinWorkspace.setError("You need to enter a join code!")
                }else {
                    binding.btnJoinWorkspace.startAnimation()
                    authViewModel.getAuthToken().observe(this, { token ->
                        authViewModel.setUser(token!!)
                        authViewModel.getUser().observe(this, { user ->
                            workspacesViewModel.joinWorkspace(token, urlJoin, user.id.toString())
                        })
                    })

                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        workspacesViewModel.workspaceState.collect {
                            when (it) {
                                is LoadingState.Loading -> {
                                    binding.btnJoinWorkspace.startAnimation()
                                }
                                is LoadingState.Success -> {
                                    showSnackbar("Workspace Joined")
                                    dismiss()
                                }
                                is LoadingState.Error -> {
                                    binding.btnJoinWorkspace.revertAnimation()
                                    showSnackbar(it.message)
                                }
                                else -> Unit
                            }
                        }
                }

//                    workspacesViewModel.getWorkspaceState().observe(this, {
//                        if (it) {
//                            Toast.makeText(requireContext(), "Join Workspace Successful", Toast.LENGTH_SHORT)
//                                .show()
//                            binding.btnJoinWorkspace.doneLoadingAnimation(
//                                resources.getColor(R.color.teal_200),
//                                ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)!!
//                                    .toBitmap()
//                            )
//                            dismiss()
//                        } else {
//                            binding.btnJoinWorkspace.revertAnimation()
//                            Toast.makeText(
//                                requireContext(),
//                                "Join Workspace Failed!!",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    })
                }
            }
            binding.imgCloseJoinWorkspace -> dismiss()
        }
    }

    fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        workspacesViewModel = ViewModelProvider(this).get(WorkspacesViewModel::class.java)
        moneyReportViewModel = ViewModelProvider(this).get(MoneyReportViewModel::class.java)
    }
}
