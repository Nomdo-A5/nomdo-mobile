package com.nomdoa5.nomdo.ui.workspace

import android.content.Context
import android.content.DialogInterface
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.databinding.DialogFragmentJoinWorkspaceBinding
import com.nomdoa5.nomdo.helpers.DismissListener
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.ui.MainActivity
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import kotlinx.coroutines.flow.collect


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class JoinWorkspaceDialogBoard : BottomSheetDialogFragment(), View.OnClickListener {
    private var _binding: DialogFragmentJoinWorkspaceBinding? = null
    private val binding get() = _binding!!
    private lateinit var workspacesViewModel: WorkspacesViewModel
    private lateinit var authViewModel: AuthViewModel
    private var listener: DismissListener? = null

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

    fun setDismissListener(listener: DismissListener) {
        this.listener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onDismiss()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnJoinWorkspace -> {
                val urlJoin = binding.editLinkJoinWorkspace.text.toString()
                if(urlJoin.isBlank()){
                    binding.editLinkJoinWorkspace.error = "You need to enter a join code!"
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
                                    (activity as MainActivity?)!!.showSnackbar("Workspace Joined")
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
                }
            }
            binding.imgCloseJoinWorkspace -> dismiss()
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(
            dialog!!.window!!.decorView, message, Snackbar.LENGTH_SHORT
        ).show()
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        workspacesViewModel = ViewModelProvider(this).get(WorkspacesViewModel::class.java)
    }
}
