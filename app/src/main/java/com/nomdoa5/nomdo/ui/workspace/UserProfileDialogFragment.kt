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
import com.nomdoa5.nomdo.databinding.DialogUserProfileWorkspaceBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.User
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.balance.MoneyReportViewModel


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class UserProfileDialogFragment : DialogFragment(), View.OnClickListener {
    private var _binding: DialogUserProfileWorkspaceBinding? = null
    private val binding get() = _binding!!
    private lateinit var moneyReportViewModel: MoneyReportViewModel
    private lateinit var workspacesViewModel: WorkspacesViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogUserProfileWorkspaceBinding.inflate(inflater, container, false)
        val root: View = binding.root
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        user = requireArguments().getParcelable("EXTRA_USER")!!
        binding.imgCloseDialogProfileWorkspace.setOnClickListener(this)
        binding.tvNameDialogProfileWorkspace.text = user.name
        binding.tvEmailDialogProfileWorkspace.text = user.email
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.imgCloseDialogProfileWorkspace -> dismiss()
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
