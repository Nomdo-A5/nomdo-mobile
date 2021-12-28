package com.nomdoa5.nomdo.ui.home

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.FragmentHomeBinding
import com.nomdoa5.nomdo.helpers.MarginItemDecoration
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.helpers.adapter.TaskCardAdapter
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Task
import com.nomdoa5.nomdo.ui.MainActivity
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.workspace.WorkspacesViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class HomeFragment : Fragment(), TaskCardAdapter.OnTaskClickListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeAdapter = TaskCardAdapter(this)
    private var homes = arrayListOf<Task>()
    private lateinit var workspacesViewModel: WorkspacesViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var homeName: Array<String>
    private lateinit var createdAt: Array<String>
    private lateinit var rvHome: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        setupViewModel()
//        setupRecyclerView()
        setupHome()
        setupOnBackPressed()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)!!.apply {
            setupToolbarMain("Home")
            setupFabMargin(0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setData() {
        homeName = resources.getStringArray(R.array.name)
        createdAt = resources.getStringArray(R.array.creator)

        for (i in homeName.indices) {
            if (i == 3) break
            val home = Task(
                i,
                homeName[i],
                "Owned by " + createdAt[i],
            )
            homes.add(home)
        }
    }

    private fun setupRecyclerView() {
        rvHome = binding.rvHomes
        rvHome.setHasFixedSize(true)
        rvHome.addItemDecoration(MarginItemDecoration(16))
        rvHome.layoutManager = LinearLayoutManager(context)
        homeAdapter.setData(homes)
        rvHome.adapter = homeAdapter
    }

    private fun setupHome(){
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            authViewModel.setUser(it!!)
            workspacesViewModel.setWorkspace(it)
        })

        authViewModel.getUser().observe(viewLifecycleOwner, {
            binding.tvNameHome.text = "Hello ${it.name}"
        })

        workspacesViewModel.getWorkspaceCount().observe(viewLifecycleOwner, {
            binding.tvTotalWorkspaceHome.text = it.toString()
        })
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[AuthViewModel::class.java]
        workspacesViewModel =
            ViewModelProvider(this)[WorkspacesViewModel::class.java]
    }

    private fun setupOnBackPressed() {
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                requireActivity().finishAffinity()
                true
            } else false
        }
    }

    override fun onTaskClick(data: Task) {

    }

    override fun onCbTaskClick(data: Task) {

    }
}