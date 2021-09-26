package com.nomdoa5.nomdo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.nomdoa5.nomdo.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding : ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignUpIn.setOnClickListener(this)
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
            binding.btnSignIn -> {
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }
    }
}