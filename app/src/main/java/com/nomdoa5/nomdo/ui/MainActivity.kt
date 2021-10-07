package com.nomdoa5.nomdo.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), View.OnClickListener,
    NavigationView.OnNavigationItemSelectedListener {

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
    private var clicked = false
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var addWorkspaceDialog: Dialog
    private lateinit var close: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addWorkspaceDialog = Dialog(this)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener(this)
        binding.appBarMain.fabAddWorkspace.setOnClickListener(this)
        binding.appBarMain.fabAddTask.setOnClickListener(this)
        binding.appBarMain.fabAddBoard.setOnClickListener(this)

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
                R.id.nav_tasks
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
            close -> {
                addWorkspaceDialog.dismiss()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_detail_task -> {
                Toast.makeText(this, "Fitur detail task belum ada ges", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.nav_settings -> {
                Toast.makeText(this, "Fitur settings belum ada ges", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.nav_logout -> {
                Toast.makeText(this, "Fitur logout belum ada ges", Toast.LENGTH_SHORT).show()
                true
            }
            else -> true
        }
    }

    fun showPopupAddWorkspace() {
        addWorkspaceDialog.setContentView(R.layout.popup_workspace)
        addWorkspaceDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        addWorkspaceDialog.show()
        close = addWorkspaceDialog.findViewById(R.id.img_close_add_workspace)
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

}