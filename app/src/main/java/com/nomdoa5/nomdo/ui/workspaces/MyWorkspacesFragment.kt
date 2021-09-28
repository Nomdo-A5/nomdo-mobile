package com.nomdoa5.nomdo.ui.workspaces

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nomdoa5.nomdo.databinding.FragmentMyWorkspacesBinding

class MyWorkspacesFragment : Fragment() {

    private lateinit var myWorkspacesViewModel: MyWorkspacesViewModel
    private var _binding: FragmentMyWorkspacesBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        myWorkspacesViewModel =
            ViewModelProvider(this).get(MyWorkspacesViewModel::class.java)

        _binding = FragmentMyWorkspacesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGallery
        myWorkspacesViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}