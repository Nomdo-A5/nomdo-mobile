package com.nomdoa5.nomdo.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.ActivitySplashScreenBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.ui.MainActivity
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.auth.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.withContext

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var authViewModel: AuthViewModel
    private var _binding: ActivitySplashScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        showLoading(true)
        authViewModel.getAuthToken().observe(this, {
            if(it.isNullOrBlank()){
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        })
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
    }

    private fun showLoading(state: Boolean){
        if(state){
            binding.progressBarSplash.visibility = View.VISIBLE
        }else{
            binding.progressBarSplash.visibility = View.GONE
        }
    }
}