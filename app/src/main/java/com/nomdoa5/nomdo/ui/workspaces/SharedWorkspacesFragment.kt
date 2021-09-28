package com.nomdoa5.nomdo.ui.workspaces

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nomdoa5.nomdo.databinding.FragmentSharedWorkspacesBinding

class SharedWorkspacesFragment : Fragment() {

    private lateinit var sharedWorkspacesViewModel: SharedWorkspacesViewModel
    private var _binding: FragmentSharedWorkspacesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedWorkspacesViewModel =
            ViewModelProvider(this).get(SharedWorkspacesViewModel::class.java)

        _binding = FragmentSharedWorkspacesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        sharedWorkspacesViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}