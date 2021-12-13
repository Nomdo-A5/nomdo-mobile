package com.nomdoa5.nomdo.ui.workspace

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nomdoa5.nomdo.databinding.DialogFragmentShareWorkspaceBinding
import com.nomdoa5.nomdo.repository.model.Workspace


class ShareWorkspaceDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {
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
        binding.editLinkShareWorkspace.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.editLinkShareWorkspace -> {
                val clipboard =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                val clip = ClipData.newPlainText("CODE_JOIN", binding.editLinkShareWorkspace.text)
                clipboard!!.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "Code join copied", Toast.LENGTH_SHORT).show()
            }
            binding.imgCloseShareWorkspace -> dismiss()
        }
    }
}
