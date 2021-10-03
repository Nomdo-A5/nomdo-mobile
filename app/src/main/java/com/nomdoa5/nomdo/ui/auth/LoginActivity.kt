package com.nomdoa5.nomdo.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.nomdoa5.nomdo.databinding.ActivityLoginBinding
import com.nomdoa5.nomdo.ui.MainActivity

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding : ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }
}