package com.nomdoa5.nomdo.ui.board

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgument
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.FragmentBoardMenuBinding
import com.nomdoa5.nomdo.ui.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class BoardMenuFragment : Fragment() {
    private lateinit var nestedNavHostFragment: NavHostFragment
    private var _binding: FragmentBoardMenuBinding? = null
    private val binding get() = _binding!!
    private val args: BoardMenuFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardMenuBinding.inflate(inflater, container, false)

        setupBottomNav()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)!!.setupToolbarMain(
            args.workspace.workspaceName!!
        )
        (activity as MainActivity?)!!.setupFabMargin(48)

        binding.navBoardView.menu.findItem(R.id.bottom_nav_dashboard).isChecked = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupBottomNav() {
        val navView = binding.navBoardView
        nestedNavHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_board_host_fragment) as NavHostFragment
        val navController = nestedNavHostFragment.navController
        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.board_navigation)
        val navArgument = NavArgument.Builder().setDefaultValue(args.workspace).build()
        graph.findNode(R.id.bottom_nav_dashboard)!!.addArgument("workspace", navArgument)
        graph.findNode(R.id.bottom_nav_board)!!.addArgument("workspace", navArgument)
        graph.findNode(R.id.bottom_nav_balance)!!.addArgument("workspace", navArgument)
        graph.findNode(R.id.bottom_nav_profile)!!.addArgument("workspace", navArgument)
        navController.graph = graph
        navView.setupWithNavController(navController)
    }

    fun getActiveFragment(): Fragment? = nestedNavHostFragment.childFragmentManager.fragments[0]

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }
}