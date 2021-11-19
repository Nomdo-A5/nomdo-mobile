package com.nomdoa5.nomdo.ui

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.ActivityMainBinding
import com.nomdoa5.nomdo.databinding.NavHeaderMainBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Workspace
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.balance.CreateBalanceDialogFragment
import com.nomdoa5.nomdo.ui.board.BoardViewModel
import com.nomdoa5.nomdo.ui.board.BoardsFragment
import com.nomdoa5.nomdo.ui.board.BoardsFragmentDirections
import com.nomdoa5.nomdo.ui.board.CreateBoardDialogFragment
import com.nomdoa5.nomdo.ui.task.CreateTaskDialogFragment
import com.nomdoa5.nomdo.ui.task.TaskViewModel
import com.nomdoa5.nomdo.ui.workspace.CreateWorkspaceDialogFragment
import com.nomdoa5.nomdo.ui.workspace.JoinWorkspaceDialogBoard
import com.nomdoa5.nomdo.ui.workspace.WorkspacesViewModel


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {
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
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var headerBinding: NavHeaderMainBinding
    private lateinit var workspaceArgument: Workspace

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
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
        binding.appBarMain.appBarLayoutWorkspace.navigation.setOnClickListener(this)
        binding.appBarMain.appBarLayoutBoard.navigation.setOnClickListener(this)
        binding.appBarMain.appBarLayout.toolbarMainNotifications.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.appBarMain.appBarLayout.toolbarMainNavigation,
            binding.appBarMain.appBarLayoutWorkspace.navigation,
            binding.appBarMain.appBarLayoutBoard.navigation -> {
                if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                } else {
                    binding.drawerLayout.closeDrawer(GravityCompat.END)
                }
            }
            binding.appBarMain.appBarLayout.toolbarMainNotifications -> {
                Toast.makeText(this, "Klik notifikasi ges", Toast.LENGTH_SHORT).show()
            }
            binding.appBarMain.fab -> {
                setFabVisibility(clicked)
                setFabAnimation(clicked)
                clicked = !clicked
            }
            binding.appBarMain.fabAddWorkspace -> {
                val addWorkspaceFragment = CreateWorkspaceDialogFragment()
                addWorkspaceFragment.show(supportFragmentManager, "Add Workspace Dialog")
            }
            binding.appBarMain.fabAddBoard -> {
                val addBoardFragment = CreateBoardDialogFragment()
                addBoardFragment.show(supportFragmentManager, "Add Board Dialog")
            }
            binding.appBarMain.fabAddTask -> {
                val addTaskFragment = CreateTaskDialogFragment()
                addTaskFragment.show(supportFragmentManager, "Add Task Dialog")
            }
            binding.appBarMain.fabAddBalance -> {
                val addBalanceFragment = CreateBalanceDialogFragment()
                addBalanceFragment.show(supportFragmentManager, "Add Balance Dialog")
            }
            binding.appBarMain.fabJoinWorkspace -> {
                val joinWorkspaceFragment = JoinWorkspaceDialogBoard()
                joinWorkspaceFragment.show(supportFragmentManager, "Join Workspace Dialog")
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
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        workspacesViewModel = ViewModelProvider(this).get(WorkspacesViewModel::class.java)
        boardsViewModel = ViewModelProvider(this).get(BoardViewModel::class.java)
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
    }

    fun setupDrawer() {
        val header = binding.navView.getHeaderView(0)
        val avatar = header.findViewById<ImageView>(R.id.nav_header_avatar)
        val name = header.findViewById<TextView>(R.id.nav_header_username)
        val email = header.findViewById<TextView>(R.id.nav_header_email)

        authViewModel.getAuthToken().observe(this, {
            mainViewModel.setUser(it!!)
        })

        mainViewModel.getUser().observe(this, {
            name.text = it.name ?: "Anonymous"
            email.text = it.email ?: "anonymous@email.com"
        })

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_my_workspaces,
                R.id.nav_shared_workspaces,
                R.id.nav_boards,
                R.id.nav_tasks,
                R.id.nav_logout
            ), binding.drawerLayout
        )

        binding.navView.setupWithNavController(navController)
    }

    private fun closeKeyboard() {
        val view: View? = this.currentFocus
        if (view != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun setupToolbarMain(title: String) {
        binding.appBarMain.appBarLayout.root.visibility = View.VISIBLE
        binding.appBarMain.appBarLayoutWorkspace.root.visibility = View.GONE
        binding.appBarMain.appBarLayoutBoard.root.visibility = View.GONE

        binding.appBarMain.appBarLayout.toolbarMainTitle.text = title
    }

    fun setupToolbarWorkspace(title: String, workspace: Workspace) {
        workspaceArgument = workspace
        binding.appBarMain.appBarLayout.root.visibility = View.GONE
        binding.appBarMain.appBarLayoutWorkspace.root.visibility = View.VISIBLE
        binding.appBarMain.appBarLayoutBoard.root.visibility = View.GONE

        binding.appBarMain.appBarLayoutWorkspace.toolbarTitle.text = title
        binding.appBarMain.appBarLayoutWorkspace.dropdown.setOnClickListener { v ->
            val popup = PopupMenu(this, v)
            popup.setOnMenuItemClickListener(this)
            popup.inflate(R.menu.workspace_menu)
            popup.show()
        }
    }

    fun setupToolbarBoard(title: String, subtitle: String) {
        binding.appBarMain.appBarLayout.root.visibility = View.GONE
        binding.appBarMain.appBarLayoutWorkspace.root.visibility = View.GONE
        binding.appBarMain.appBarLayoutBoard.root.visibility = View.VISIBLE

        binding.appBarMain.appBarLayoutBoard.toolbarTitle.text = title
        binding.appBarMain.appBarLayoutBoard.toolbarSubtitle.text = "in $subtitle"
        binding.appBarMain.appBarLayoutBoard.dropdown.setOnClickListener { v ->
            val popup = PopupMenu(this, v)
            popup.setOnMenuItemClickListener(this)
            popup.inflate(R.menu.workspace_menu)
            popup.show()
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.money_report_menu_workspace -> {
                Toast.makeText(this, "Ngeklik board", Toast.LENGTH_SHORT).show()
                val navHostFragment: Fragment? =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)

                val fragment = navHostFragment?.childFragmentManager?.fragments?.get(0)

                if (fragment is BoardsFragment) {
                    val action =
                        BoardsFragmentDirections.actionNavBoardsToMoneyReportFragment(
                            workspaceArgument
                        )
                    Navigation.findNavController(findViewById(R.id.nav_host_fragment_content_main))
                        .navigate(action)
                }


            }
            R.id.board_menu_workspace -> {
                Toast.makeText(this, "Ngeklik money report", Toast.LENGTH_SHORT).show()
            }
            R.id.member_menu_workspace -> {
                Toast.makeText(this, "Ngeklik member", Toast.LENGTH_SHORT).show()
                val navHostFragment: Fragment? =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)

                val fragment = navHostFragment?.childFragmentManager?.fragments?.get(0)

                if (fragment is BoardsFragment) {
                    val action =
                        BoardsFragmentDirections.actionNavBoardsToProfileWorkspaceFragment()
                    Navigation.findNavController(findViewById(R.id.nav_host_fragment_content_main))
                        .navigate(action)
                }

            }
        }
        return true
    }
}