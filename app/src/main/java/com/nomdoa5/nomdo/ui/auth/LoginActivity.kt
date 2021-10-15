package com.nomdoa5.nomdo.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.nomdoa5.nomdo.databinding.ActivityLoginBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.request.LoginRequest
import com.nomdoa5.nomdo.ui.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var authViewModel: AuthViewModel
    private var _binding : ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()

        binding.btnSignUpIn.setOnClickListener(this)
        binding.tvForgot.setOnClickListener(this)
        binding.btnSignIn.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when(v){
            binding.btnSignUpIn -> {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
            binding.tvForgot -> {
                startActivity(Intent(this, ForgetPassActivity::class.java))
            }
            binding.btnSignIn -> {
                val email = binding.editUsername.text.toString()
                val password = binding.editPassword.text.toString()
                val login = LoginRequest(email, password)
                authViewModel.login(login)

                authViewModel.getLoginState().observe(this, {
                    if(it){
                        authViewModel.getAuthToken().observe(this, {
                            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                        })
                    }else{
                        Toast.makeText(this, "Login Failed!!", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    fun setupViewModel(){
        val pref = UserPreferences.getInstance(dataStore)
        authViewModel = ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
    }
}