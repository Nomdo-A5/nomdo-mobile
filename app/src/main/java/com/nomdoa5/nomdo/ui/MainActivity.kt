package com.nomdoa5.nomdo.ui

import NoFilterAdapter
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.GravityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.apachat.loadingbutton.core.customViews.CircularProgressButton
import com.google.android.material.textfield.TextInputEditText
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.ActivityMainBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.request.board.BoardRequest
import com.nomdoa5.nomdo.repository.model.request.task.TaskRequest
import com.nomdoa5.nomdo.repository.model.request.workspace.WorkspaceRequest
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.boards.BoardsViewModel
import com.nomdoa5.nomdo.ui.dialog.CreateBoardDialogFragment
import com.nomdoa5.nomdo.ui.dialog.CreateTaskDialogFragment
import com.nomdoa5.nomdo.ui.dialog.CreateWorkspaceDialogFragment
import com.nomdoa5.nomdo.ui.tasks.TasksViewModel
import com.nomdoa5.nomdo.ui.workspaces.WorkspacesViewModel


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_botton_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_botton_anim
        )
    }
    private lateinit var authViewModel: AuthViewModel
    private lateinit var workspacesViewModel: WorkspacesViewModel
    private lateinit var boardsViewModel: BoardsViewModel
    private lateinit var tasksViewModel: TasksViewModel
    private var clicked = false
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var addWorkspaceDialog: Dialog
    private lateinit var addBoardDialog: Dialog
    private lateinit var addTaskDialog: Dialog
    private lateinit var closeWorkspace: ImageView
    private lateinit var closeBoard: ImageView
    private lateinit var closeTask: ImageView
    private lateinit var btnAddWorkspace: CircularProgressButton
    private lateinit var btnAddBoard: CircularProgressButton
    private lateinit var btnAddTask: CircularProgressButton
    private lateinit var spinnerWorkspace: AutoCompleteTextView
    private lateinit var spinnerBoard: AutoCompleteTextView
    private lateinit var workspaceAdapter: NoFilterAdapter
    private lateinit var boardAdapter: NoFilterAdapter
    private lateinit var board: BoardRequest
    private lateinit var workspace: WorkspaceRequest
    private val workspaceAdapterId = ArrayList<String>()
    private val boardAdapterId = ArrayList<String>()
    private var spinnerBoardPosition: Int? = null
    private var spinnerWorkspacePosition: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addWorkspaceDialog = Dialog(this)
        addBoardDialog = Dialog(this)
        addTaskDialog = Dialog(this)

        setSupportActionBar(binding.appBarMain.appBarLayout.toolbar)
        setupViewModel()
        setupDrawer()

        binding.appBarMain.fab.setOnClickListener(this)
        binding.appBarMain.fabAddWorkspace.setOnClickListener(this)
        binding.appBarMain.fabAddTask.setOnClickListener(this)
        binding.appBarMain.fabAddBoard.setOnClickListener(this)
        binding.appBarMain.fabAddTask.setOnClickListener(this)
        binding.appBarMain.appBarLayout.navigation.setOnClickListener(this)
        binding.appBarMain.appBarLayout.notifications.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.appBarMain.appBarLayout.navigation -> {
                if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                } else {
                    binding.drawerLayout.closeDrawer(GravityCompat.END)
                }
            }
            binding.appBarMain.appBarLayout.notifications -> {
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
            closeWorkspace -> {
                addWorkspaceDialog.dismiss()
            }
            closeBoard -> {
                addBoardDialog.dismiss()
            }
            closeTask -> {
                addTaskDialog.dismiss()
            }
            btnAddWorkspace -> {
                closeKeyboard()
                btnAddWorkspace.startAnimation()
                val workspaceName =
                    addWorkspaceDialog.findViewById<TextInputEditText>(R.id.edit_name_add_workspace).text.toString()
                val workspace = WorkspaceRequest(workspaceName)
                authViewModel.getAuthToken().observe(this, {
                    workspacesViewModel.addWorkspace(it!!, workspace)
                })

                workspacesViewModel.getAddWorkspaceState().observe(this, {
                    if (it) {
                        Toast.makeText(this, "Workspace Added", Toast.LENGTH_SHORT).show()
                        btnAddWorkspace.doneLoadingAnimation(
                            resources.getColor(R.color.teal_200),
                            ContextCompat.getDrawable(this, R.drawable.ic_check)!!
                                .toBitmap()
                        )
                        addWorkspaceDialog.dismiss()
                    } else {
                        btnAddWorkspace.revertAnimation()
                        Toast.makeText(this, "Add Workspace Failed!!", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            btnAddBoard -> {
                closeKeyboard()
                btnAddBoard.startAnimation()
                val boardName =
                    addBoardDialog.findViewById<TextInputEditText>(R.id.edit_name_add_board).text.toString()

                board = BoardRequest(boardName, spinnerWorkspacePosition!!)
                authViewModel.getAuthToken().observe(this, {
                    boardsViewModel.addBoard(it!!, board)
                })

                boardsViewModel.getAddBoardState().observe(this, {
                    if (it) {
                        Toast.makeText(this, "Board Added", Toast.LENGTH_SHORT).show()
                        btnAddBoard.doneLoadingAnimation(
                            resources.getColor(R.color.teal_200),
                            ContextCompat.getDrawable(this, R.drawable.ic_check)!!
                                .toBitmap()
                        )
                        Handler(Looper.getMainLooper()).postDelayed({
                            addBoardDialog.dismiss()
                        }, 1000)
                    } else {
                        btnAddBoard.revertAnimation()
                        Toast.makeText(this, "Add Board Failed!!", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            btnAddTask -> {
                closeKeyboard()
                btnAddTask.startAnimation()

                val taskName =
                    addTaskDialog.findViewById<TextInputEditText>(R.id.edit_name_add_task).text.toString()
                val taskDescription =
                    addTaskDialog.findViewById<TextInputEditText>(R.id.edit_desc_add_task).text.toString()
                val task = TaskRequest(taskName, taskDescription, spinnerBoardPosition)
                authViewModel.getAuthToken().observe(this, {
                    tasksViewModel.addTask(it!!, task)
                })

                tasksViewModel.getAddTaskState().observe(this, {
                    if (it) {
                        Toast.makeText(this, "Task Added", Toast.LENGTH_SHORT).show()
                        btnAddTask.doneLoadingAnimation(
                            resources.getColor(R.color.teal_200),
                            ContextCompat.getDrawable(this, R.drawable.ic_check)!!
                                .toBitmap()
                        )
                        Handler(Looper.getMainLooper()).postDelayed({
                            addTaskDialog.dismiss()
                        }, 1000)
                    } else {
                        btnAddTask.revertAnimation()
                        Toast.makeText(this, "Add Task Failed!!", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun setFabAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.appBarMain.fabAddBoard.visibility = View.VISIBLE
            binding.appBarMain.fabAddTask.visibility = View.VISIBLE
            binding.appBarMain.fabAddWorkspace.visibility = View.VISIBLE
            binding.appBarMain.tvAddBoard.visibility = View.VISIBLE
            binding.appBarMain.tvAddTask.visibility = View.VISIBLE
            binding.appBarMain.tvAddWorkspace.visibility = View.VISIBLE
        } else {
            binding.appBarMain.fabAddBoard.visibility = View.GONE
            binding.appBarMain.fabAddTask.visibility = View.GONE
            binding.appBarMain.fabAddWorkspace.visibility = View.GONE
            binding.appBarMain.tvAddBoard.visibility = View.GONE
            binding.appBarMain.tvAddTask.visibility = View.GONE
            binding.appBarMain.tvAddWorkspace.visibility = View.GONE
        }
    }

    private fun setFabVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.appBarMain.fabAddBoard.startAnimation(fromBottom)
            binding.appBarMain.fabAddTask.startAnimation(fromBottom)
            binding.appBarMain.fabAddWorkspace.startAnimation(fromBottom)
            binding.appBarMain.tvAddBoard.startAnimation(fromBottom)
            binding.appBarMain.tvAddTask.startAnimation(fromBottom)
            binding.appBarMain.tvAddWorkspace.startAnimation(fromBottom)
            binding.appBarMain.fab.startAnimation(rotateOpen)
        } else {
            binding.appBarMain.fabAddBoard.startAnimation(toBottom)
            binding.appBarMain.fabAddTask.startAnimation(toBottom)
            binding.appBarMain.fabAddWorkspace.startAnimation(toBottom)
            binding.appBarMain.tvAddBoard.startAnimation(toBottom)
            binding.appBarMain.tvAddTask.startAnimation(toBottom)
            binding.appBarMain.tvAddWorkspace.startAnimation(toBottom)
            binding.appBarMain.fab.startAnimation(rotateClose)
        }
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        workspacesViewModel = ViewModelProvider(this).get(WorkspacesViewModel::class.java)
        boardsViewModel = ViewModelProvider(this).get(BoardsViewModel::class.java)
        tasksViewModel = ViewModelProvider(this).get(TasksViewModel::class.java)
    }

    fun setupDrawer() {
        val header = binding.navView.getHeaderView(0)
        val profile = header.findViewById<ImageView>(R.id.nav_header_avatar)
        val name = header.findViewById<TextView>(R.id.nav_header_username)
        val email = header.findViewById<TextView>(R.id.nav_header_email)

        authViewModel.getAuthToken().observe(this, {
            authViewModel.setUser(it!!)
        })

        email.text = "saohu"
        authViewModel.getUser().observe(this, {
            name.setText(it[0].name.toString())
            email.text = "it[0].email.toString()"
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

    fun fragmentMethod() {
        Toast.makeText(
            this@MainActivity, "Method called From Fragment",
            Toast.LENGTH_LONG
        ).show()
        binding.appBarMain.appBarLayout.toolbarTitle.setText("Shared workspace gan")
        binding.appBarMain.appBarLayout.toolbarTitle.setOnClickListener { v ->
            val popup = PopupMenu(this, v)
            popup.setOnMenuItemClickListener(this)
            popup.inflate(R.menu.workspace_menu)
            popup.show()
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.board_menu_workspace -> {
                Toast.makeText(this, "Ngeklik board", Toast.LENGTH_SHORT).show()
            }
            R.id.money_report_menu_workspace -> {
                Toast.makeText(this, "Ngeklik money report", Toast.LENGTH_SHORT).show()
            }
            R.id.member_menu_workspace -> {
                Toast.makeText(this, "Ngeklik member", Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }
}