package com.nomdoa5.nomdo.ui.auth

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.nomdoa5.nomdo.databinding.ActivityRegisterBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.request.RegisterRequest

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding : ActivityRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel : AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        binding.imgBack.setOnClickListener(this)
        binding.btnSignUp.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when(v){
            binding.imgBack -> {
                finish()
            }
            binding.btnSignUp -> {
                closeKeyboard()
                binding.btnSignUp.startAnimation()
                val username = binding.editUsernameSignUp.text.toString()
                val email = binding.editEmailSignUp.text.toString()
                val password = binding.editPasswordSignUp.text.toString()
                val passwordConfirmation = binding.editConfirmSignUp.text.toString()
                val register = RegisterRequest(username, email, password, passwordConfirmation)
                authViewModel.register(register)

                authViewModel.getRegisterStatus().observe(this, {
                    if(it){
                        binding.btnSignUp.revertAnimation()
                        Toast.makeText(this, "Register Success", Toast.LENGTH_SHORT).show()
                        finish()
                    }else{
                        binding.btnSignUp.revertAnimation()
                        Toast.makeText(this, "Register Failed!", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    fun setupViewModel(){
        val pref = UserPreferences.getInstance(dataStore)
        authViewModel = ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
    }

    private fun closeKeyboard() {
        val view: View? = this.currentFocus
        if (view != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}