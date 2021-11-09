package com.nomdoa5.nomdo.ui.workspace

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.DialogFragmentUpdateWorkspaceBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Workspace
import com.nomdoa5.nomdo.repository.model.request.workspace.UpdateWorkspaceRequest
import com.nomdoa5.nomdo.ui.auth.AuthViewModel


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
                val newWorkspace = UpdateWorkspaceRequest(workspace.id, newWorkspaceTitle)

                authViewModel.getAuthToken().observe(this, {
                    workspacesViewModel.updateWorkspace(it!!, newWorkspace)
                })

                workspacesViewModel.getUpdateWorkspaceState()
                    .observe(this, object : Observer<Boolean?> {
                        override fun onChanged(isLoading: Boolean?) {
                            if (isLoading!!) {
                                Toast.makeText(
                                    requireContext(),
                                    "Update Workspace Success!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.btnUpdateWorkspace.doneLoadingAnimation(
                                    resources.getColor(R.color.teal_200),
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_check
                                    )!!
                                        .toBitmap()
                                )
                                dismiss()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Update Workspace Failed!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                binding.btnUpdateWorkspace.revertAnimation()
                            }
                        }
                    })

            }
            binding.btnDeleteWorkspace -> {
                binding.btnDeleteWorkspace.startAnimation()
                authViewModel.getAuthToken().observe(this, {
                    workspacesViewModel.deleteWorkspace(it!!, workspace.id.toString())
                })

                workspacesViewModel.getDeleteWorkspaceState().observe(this, {
                    if(it){
                        Toast.makeText(
                            requireContext(),
                            "Delete Workspace Success",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.btnUpdateWorkspace.doneLoadingAnimation(
                            resources.getColor(R.color.teal_200),
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_check
                            )!!
                                .toBitmap()
                        )
                        dismiss()
                    }else{
                        Toast.makeText(
                            requireContext(),
                            "Delete Workspace Failed!",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.btnUpdateWorkspace.revertAnimation()
                        dismiss()
                    }
                })
                dismiss()
            }
            binding.imgCloseUpdateWorkspace -> {
                dismiss()
            }
        }
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        workspacesViewModel = ViewModelProvider(this).get(WorkspacesViewModel::class.java)
    }
}