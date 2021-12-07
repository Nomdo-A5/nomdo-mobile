package com.nomdoa5.nomdo.ui.workspace

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.databinding.DialogFragmentUpdateWorkspaceBinding
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Workspace
import com.nomdoa5.nomdo.repository.model.request.workspace.UpdateWorkspaceRequest
import com.nomdoa5.nomdo.ui.MainActivity
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import kotlinx.coroutines.flow.collect


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class UpdateWorkspaceDialogFragment : DialogFragment(), View.OnClickListener {
    private var _binding: DialogFragmentUpdateWorkspaceBinding? = null
    private val binding get() = _binding!!
    private lateinit var workspacesViewModel: WorkspacesViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var workspace: Workspace

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentUpdateWorkspaceBinding.inflate(inflater, container, false)
        val root: View = binding.root
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workspace = requireArguments().getParcelable("EXTRA_WORKSPACE")!!
        binding.editNameUpdateWorkspace.setText(workspace.workspaceName)
        binding.btnUpdateWorkspace.setOnClickListener(this)
        binding.btnDeleteWorkspace.setOnClickListener(this)
        binding.imgCloseUpdateWorkspace.setOnClickListener(this)
        setupViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnUpdateWorkspace -> {
                binding.btnUpdateWorkspace.startAnimation()
                val newWorkspaceTitle = binding.editNameUpdateWorkspace.text.toString()
                val newWorkspaceDescription = binding.editDescUpdateWorkspace.text.toString()
                val newWorkspace =
                    UpdateWorkspaceRequest(workspace.id, newWorkspaceTitle, newWorkspaceDescription)

                authViewModel.getAuthToken().observe(this, {
                    workspacesViewModel.updateWorkspace(it!!, newWorkspace)
                })


                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    workspacesViewModel.workspaceState.collect {
                        when (it) {
                            is LoadingState.Loading -> {
                                binding.btnUpdateWorkspace.startAnimation()
                            }
                            is LoadingState.Success -> {
                                (activity as MainActivity?)!!.showSnackbar("Workspace Updated")
                                dismiss()
                            }
                            is LoadingState.Error -> {
                                binding.btnUpdateWorkspace.revertAnimation()
                                showSnackbar(it.message)
                            }
                            else -> Unit
                        }
                    }
                }

            }
            binding.btnDeleteWorkspace -> {
                binding.btnDeleteWorkspace.startAnimation()
                authViewModel.getAuthToken().observe(this, {
                    workspacesViewModel.deleteWorkspace(it!!, workspace.id.toString())
                })

                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    workspacesViewModel.workspaceState.collect {
                        when (it) {
                            is LoadingState.Loading -> {
                                binding.btnDeleteWorkspace.startAnimation()
                            }
                            is LoadingState.Success -> {
                                (activity as MainActivity?)!!.showSnackbar("Workspace Deleted")
                                dismiss()
                            }
                            is LoadingState.Error -> {
                                binding.btnDeleteWorkspace.revertAnimation()
                                showSnackbar(it.message)
                            }
                            else -> Unit
                        }
                    }
                }
                dismiss()
            }
            binding.imgCloseUpdateWorkspace -> {
                dismiss()
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        workspacesViewModel = ViewModelProvider(this).get(WorkspacesViewModel::class.java)
    }
}