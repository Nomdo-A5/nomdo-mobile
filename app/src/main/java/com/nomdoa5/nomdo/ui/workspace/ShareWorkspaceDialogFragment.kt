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
import com.nomdoa5.nomdo.databinding.DialogFragmentShareWorkspaceBinding
import com.nomdoa5.nomdo.repository.model.Workspace


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class ShareWorkspaceDialogFragment : DialogFragment(), View.OnClickListener {
    private var _binding: DialogFragmentShareWorkspaceBinding? = null
    private val binding get() = _binding!!
    private lateinit var workspace: Workspace

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentShareWorkspaceBinding.inflate(inflater, container, false)
        val root: View = binding.root
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workspace = requireArguments().getParcelable("EXTRA_WORKSPACE")!!

        binding.imgCloseShareWorkspace.setOnClickListener(this)
        binding.editLinkShareWorkspace.setText(workspace.urlJoin)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.imgCloseShareWorkspace -> dismiss()
        }
    }
}
