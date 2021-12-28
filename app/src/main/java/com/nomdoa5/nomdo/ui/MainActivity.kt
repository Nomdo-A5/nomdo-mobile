package com.nomdoa5.nomdo.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.ActivityMainBinding
import com.nomdoa5.nomdo.databinding.NavHeaderMainBinding
import com.nomdoa5.nomdo.helpers.DismissListener
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.helpers.toDp
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.balance.CreateBalanceDialogFragment
import com.nomdoa5.nomdo.ui.balance.MoneyReportFragment
import com.nomdoa5.nomdo.ui.balance.MoneyReportPageFragment
import com.nomdoa5.nomdo.ui.board.BoardMenuFragment
import com.nomdoa5.nomdo.ui.board.BoardViewModel
import com.nomdoa5.nomdo.ui.board.BoardsFragment
import com.nomdoa5.nomdo.ui.board.CreateBoardDialogFragment
import com.nomdoa5.nomdo.ui.search.SearchActivity
import com.nomdoa5.nomdo.ui.task.*
import com.nomdoa5.nomdo.ui.workspace.CreateWorkspaceDialogFragment
import com.nomdoa5.nomdo.ui.workspace.JoinWorkspaceDialogBoard
import com.nomdoa5.nomdo.ui.workspace.MyWorkspacesFragment
import com.nomdoa5.nomdo.ui.workspace.WorkspacesViewModel


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity(), View.OnClickListener,
    NavigationView.OnNavigationItemSelectedListener, DismissListener {
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            application.applicationContext,
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            application.applicationContext,
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            application.applicationContext,
            R.anim.from_botton_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            application.applicationContext,
            R.anim.to_botton_anim
        )
    }
    private lateinit var authViewModel: AuthViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var workspacesViewModel: WorkspacesViewModel
    private lateinit var boardsViewModel: BoardViewModel
    private lateinit var taskViewModel: TaskViewModel
    private var clicked = false
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var headerBinding: NavHeaderMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        headerBinding = DataBindingUtil.setContentView(this, R.layout.nav_header_main)
        setupViewModel()

        setContentView(binding.root)

        setupToolbarMain("Nomdo")
        setupDrawer()

        binding.appBarMain.fab.setOnClickListener(this)
        binding.appBarMain.fabAddWorkspace.setOnClickListener(this)
        binding.appBarMain.fabAddTask.setOnClickListener(this)
        binding.appBarMain.fabAddBoard.setOnClickListener(this)
        binding.appBarMain.fabAddTask.setOnClickListener(this)
        binding.appBarMain.fabAddBalance.setOnClickListener(this)
        binding.appBarMain.fabJoinWorkspace.setOnClickListener(this)
        binding.appBarMain.appBarLayout.toolbarMainNavigation.setOnClickListener(this)
        binding.appBarMain.appBarLayoutBoard.navigation.setOnClickListener(this)
        binding.appBarMain.appBarLayoutBoard.toolbarBoardSearch.setOnClickListener(this)
        binding.appBarMain.appBarLayout.toolbarMainSearch.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.appBarMain.appBarLayout.toolbarMainNavigation,
            binding.appBarMain.appBarLayoutBoard.navigation -> {
                if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                } else {
                    binding.drawerLayout.closeDrawer(GravityCompat.END)
                }
            }
            binding.appBarMain.appBarLayout.toolbarMainSearch,
            binding.appBarMain.appBarLayoutBoard.toolbarBoardSearch -> {
                startActivity(Intent(this, SearchActivity::class.java))
            }
            binding.appBarMain.fab -> {
                setupFabClick()
            }
            binding.appBarMain.fabAddWorkspace -> {
                val addWorkspaceFragment = CreateWorkspaceDialogFragment()
                addWorkspaceFragment.showNow(supportFragmentManager, "Add Workspace Dialog")
                addWorkspaceFragment.dialog!!.setCanceledOnTouchOutside(false)
                addWorkspaceFragment.setDismissListener(this)
                setupFabClick()
            }
            binding.appBarMain.fabAddBoard -> {
                val addBoardFragment = CreateBoardDialogFragment()
                addBoardFragment.showNow(supportFragmentManager, "Add Board Dialog")
                addBoardFragment.dialog!!.setCanceledOnTouchOutside(false)
                setupFabClick()
            }
            binding.appBarMain.fabAddTask -> {
                val addTaskFragment = CreateTaskDialogFragment()
                addTaskFragment.showNow(supportFragmentManager, "Add Task Dialog")
                addTaskFragment.dialog!!.setCanceledOnTouchOutside(false)
                addTaskFragment.setDismissListener(this)
                setupFabClick()
            }
            binding.appBarMain.fabAddBalance -> {
                val addBalanceFragment = CreateBalanceDialogFragment()
                addBalanceFragment.showNow(supportFragmentManager, "Add Balance Dialog")
                addBalanceFragment.dialog!!.setCanceledOnTouchOutside(false)
                setupFabClick()
            }
            binding.appBarMain.fabJoinWorkspace -> {
                val joinWorkspaceFragment = JoinWorkspaceDialogBoard()
                joinWorkspaceFragment.showNow(supportFragmentManager, "Join Workspace Dialog")
                joinWorkspaceFragment.dialog!!.setCanceledOnTouchOutside(false)
                setupFabClick()
            }
        }
    }

    private fun setFabAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.appBarMain.fabAddBoard.visibility = View.VISIBLE
            binding.appBarMain.fabAddTask.visibility = View.VISIBLE
            binding.appBarMain.fabAddWorkspace.visibility = View.VISIBLE
            binding.appBarMain.fabAddBalance.visibility = View.VISIBLE
            binding.appBarMain.fabJoinWorkspace.visibility = View.VISIBLE
            binding.appBarMain.tvAddBoard.visibility = View.VISIBLE
            binding.appBarMain.tvAddTask.visibility = View.VISIBLE
            binding.appBarMain.tvAddWorkspace.visibility = View.VISIBLE
            binding.appBarMain.tvAddBalance.visibility = View.VISIBLE
            binding.appBarMain.tvJoinWorkspace.visibility = View.VISIBLE
        } else {
            binding.appBarMain.fabAddBoard.visibility = View.GONE
            binding.appBarMain.fabAddTask.visibility = View.GONE
            binding.appBarMain.fabAddWorkspace.visibility = View.GONE
            binding.appBarMain.fabAddBalance.visibility = View.GONE
            binding.appBarMain.fabJoinWorkspace.visibility = View.GONE
            binding.appBarMain.tvAddBoard.visibility = View.GONE
            binding.appBarMain.tvAddTask.visibility = View.GONE
            binding.appBarMain.tvAddWorkspace.visibility = View.GONE
            binding.appBarMain.tvAddBalance.visibility = View.GONE
            binding.appBarMain.tvJoinWorkspace.visibility = View.GONE
        }
    }

    private fun setFabVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.appBarMain.fabAddBoard.startAnimation(fromBottom)
            binding.appBarMain.fabAddTask.startAnimation(fromBottom)
            binding.appBarMain.fabAddWorkspace.startAnimation(fromBottom)
            binding.appBarMain.fabAddBalance.startAnimation(fromBottom)
            binding.appBarMain.fabJoinWorkspace.startAnimation(fromBottom)
            binding.appBarMain.tvAddBoard.startAnimation(fromBottom)
            binding.appBarMain.tvAddTask.startAnimation(fromBottom)
            binding.appBarMain.tvAddWorkspace.startAnimation(fromBottom)
            binding.appBarMain.tvAddBalance.startAnimation(fromBottom)
            binding.appBarMain.tvJoinWorkspace.startAnimation(fromBottom)
            binding.appBarMain.fab.startAnimation(rotateOpen)
        } else {
            binding.appBarMain.fabAddBoard.startAnimation(toBottom)
            binding.appBarMain.fabAddTask.startAnimation(toBottom)
            binding.appBarMain.fabAddWorkspace.startAnimation(toBottom)
            binding.appBarMain.fabAddBalance.startAnimation(toBottom)
            binding.appBarMain.fabJoinWorkspace.startAnimation(toBottom)
            binding.appBarMain.tvAddBoard.startAnimation(toBottom)
            binding.appBarMain.tvAddTask.startAnimation(toBottom)
            binding.appBarMain.tvAddWorkspace.startAnimation(toBottom)
            binding.appBarMain.tvAddBalance.startAnimation(toBottom)
            binding.appBarMain.tvJoinWorkspace.startAnimation(toBottom)
            binding.appBarMain.fab.startAnimation(rotateClose)
        }
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[AuthViewModel::class.java]
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        workspacesViewModel = ViewModelProvider(this)[WorkspacesViewModel::class.java]
        boardsViewModel = ViewModelProvider(this)[BoardViewModel::class.java]
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
    }

    private fun setupDrawer() {
        val header = binding.navView.getHeaderView(0)
        val avatar = header.findViewById<ImageView>(R.id.nav_header_avatar)
        val name = header.findViewById<TextView>(R.id.nav_header_username)
        val email = header.findViewById<TextView>(R.id.nav_header_email)

        binding.navView.menu.findItem(R.id.nav_home).isChecked = true

        authViewModel.getAuthToken().observe(this, {
            if (!it.isNullOrBlank()) {
                mainViewModel.setUser(it)
            }
        })

        mainViewModel.getUser().observe(this, {
            name.text = it.name ?: "Anonymous"
            email.text = it.email ?: "anonymous@email.com"
        })

        binding.navView.setNavigationItemSelectedListener(this)
    }

    fun closeKeyboard() {
        val view: View? = this.currentFocus
        if (view != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun setupToolbarMain(title: String) {
        binding.appBarMain.appBarLayout.root.visibility = View.VISIBLE
        binding.appBarMain.appBarLayoutBoard.root.visibility = View.GONE

        binding.appBarMain.appBarLayout.toolbarMainTitle.text = title
    }

    fun setupToolbarBoard(title: String, subtitle: String) {
        binding.appBarMain.appBarLayout.root.visibility = View.GONE
        binding.appBarMain.appBarLayoutBoard.root.visibility = View.VISIBLE

        binding.appBarMain.appBarLayoutBoard.toolbarTitle.text = title
        binding.appBarMain.appBarLayoutBoard.toolbarSubtitle.text = "in $subtitle"
    }

    fun setupFabMargin(height: Int) {
        val layoutParams = binding.appBarMain.fab.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.rightMargin = 24.toDp()
        layoutParams.bottomMargin = (24 + height).toDp()

        binding.appBarMain.fab.layoutParams = layoutParams
    }

    fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_settings -> startActivity(Intent(this, SettingActivity::class.java))
            R.id.nav_logout -> {
                val addDialogFragment = LogoutFragment()
                addDialogFragment.show(supportFragmentManager, "Logout Dialog")
            }
        }
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        NavigationUI.onNavDestinationSelected(item, navController)
        binding.drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

    private fun setupFabClick() {
        setFabVisibility(clicked)
        setFabAnimation(clicked)
        clicked = !clicked
    }

    override fun onDismiss() {
        val navHostFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)

        when (val fragment = navHostFragment?.childFragmentManager?.fragments?.get(0)) {
            is MyWorkspacesFragment -> {
                fragment.dispatchRefresh()
            }
            is TaskFragment -> {
                val taskPageFragment = fragment.childFragmentManager.fragments[0]
                val taskPageDoneFragment = fragment.childFragmentManager.fragments[1]

                if(taskPageFragment is TaskPageFragment){
                    taskPageFragment.dispatchRefresh()
                }
                if(taskPageDoneFragment is TaskDonePageFragment){
                    taskPageDoneFragment.dispatchRefresh()
                }
            }
            is BoardMenuFragment -> {
                val fragment1 = fragment.childFragmentManager.fragments[0]
                if(fragment1 is BoardsFragment){
                    fragment1.dispatchRefresh()
                    return
                }

                if(fragment1 is MoneyReportPageFragment) {
                    val fragment2 = fragment.childFragmentManager.fragments[1]
                    val fragment3 = fragment.childFragmentManager.fragments[2]

                    fragment1.dispatchRefresh()
                    if (fragment2 is MoneyReportPageFragment) {
                        fragment2.dispatchRefresh()
                    }
                    if (fragment3 is MoneyReportPageFragment) {
                        fragment3.dispatchRefresh()
                    }
                }
            }
        }
    }
}