package com.nomdoa5.nomdo.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
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

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_my_workspaces, R.id.nav_shared_workspaces, R.id.nav_boards, R.id.nav_tasks
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
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.nav_article ->{
                Toast.makeText(this, "Fitur article belum ada ges", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.nav_settings ->{
                Toast.makeText(this, "Fitur settings belum ada ges", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.nav_logout ->{
                Toast.makeText(this, "Fitur logout belum ada ges", Toast.LENGTH_SHORT).show()
                true
            }
            else -> true
        }
    }
}