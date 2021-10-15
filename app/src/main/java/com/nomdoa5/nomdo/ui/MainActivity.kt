package com.nomdoa5.nomdo.ui

import NoFilterAdapter
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.ActivityMainBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.auth.LoginActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity(), View.OnClickListener{
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
    private var clicked = false
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var addWorkspaceDialog: Dialog
    private lateinit var addBoardDialog: Dialog
    private lateinit var addTaskDialog: Dialog
    private lateinit var close: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addWorkspaceDialog = Dialog(this)
        addBoardDialog = Dialog(this)
        addTaskDialog = Dialog(this)

        setSupportActionBar(binding.appBarMain.toolbar)
        setupViewModel()

        binding.appBarMain.fab.setOnClickListener(this)
        binding.appBarMain.fabAddWorkspace.setOnClickListener(this)
        binding.appBarMain.fabAddTask.setOnClickListener(this)
        binding.appBarMain.fabAddBoard.setOnClickListener(this)
        binding.appBarMain.fabAddTask.setOnClickListener(this)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_my_workspaces,
                R.id.nav_shared_workspaces,
                R.id.nav_boards,
                R.id.nav_tasks,
                R.id.nav_logout
            ), drawerLayout
        )

        binding.appBarMain.navigation.setOnClickListener(this)
        binding.appBarMain.notifications.setOnClickListener(this)
        navView.setupWithNavController(navController)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.appBarMain.navigation -> {
                if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                } else {
                    binding.drawerLayout.closeDrawer(GravityCompat.END)
                }
            }
            binding.appBarMain.notifications -> {
                Toast.makeText(this, "Klik notifikasi ges", Toast.LENGTH_SHORT).show()
            }
            binding.appBarMain.fab -> {
                setFabVisibility(clicked)
                setFabAnimation(clicked)
                clicked = !clicked
            }
            binding.appBarMain.fabAddWorkspace -> {
                showPopupAddWorkspace()
            }
            binding.appBarMain.fabAddBoard -> {
                showPopupAddBoard()
            }
            binding.appBarMain.fabAddTask -> {
                showPopupAddTask()
            }
            close -> {
                addWorkspaceDialog.dismiss()
                addBoardDialog.dismiss()
                addTaskDialog.dismiss()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    fun showPopupAddWorkspace() {
        addWorkspaceDialog.setContentView(R.layout.popup_workspace)
        addWorkspaceDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        addWorkspaceDialog.show()
        close = addWorkspaceDialog.findViewById(R.id.img_close_add_workspace)
        close.setOnClickListener(this)
    }

    fun showPopupAddBoard() {
        addBoardDialog.setContentView(R.layout.popup_board)
        addBoardDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val spinner =
            addBoardDialog.findViewById<AutoCompleteTextView>(R.id.spinner_workspace_add_board)
        spinner.setAdapter(
            NoFilterAdapter(
                this,
                R.layout.item_dropdown,
                resources.getStringArray(R.array.name)
            )
        )
        spinner.setText("Alwan Fauzi", false)

        addBoardDialog.show()
        close = addBoardDialog.findViewById(R.id.img_close_add_board)
        close.setOnClickListener(this)
    }

    fun showPopupAddTask() {
        addTaskDialog.setContentView(R.layout.popup_task)
        addTaskDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val spinnerWorkspace =
            addTaskDialog.findViewById<AutoCompleteTextView>(R.id.spinner_workspace_add_task)
        spinnerWorkspace.setAdapter(
            NoFilterAdapter(
                this,
                R.layout.item_dropdown,
                resources.getStringArray(R.array.name)
            )
        )
        spinnerWorkspace.setText("Fauzi", false)

        val spinnerBoard =
            addTaskDialog.findViewById<AutoCompleteTextView>(R.id.spinner_board_add_task)
        spinnerBoard.setAdapter(
            NoFilterAdapter(
                this,
                R.layout.item_dropdown,
                resources.getStringArray(R.array.creator)
            )
        )
        spinnerBoard.setText("Alwan", false)

        addTaskDialog.show()
        close = addTaskDialog.findViewById(R.id.img_close_add_task)
        close.setOnClickListener(this)
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

    fun setupViewModel(){
        val pref = UserPreferences.getInstance(dataStore)
        authViewModel = ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
    }
}