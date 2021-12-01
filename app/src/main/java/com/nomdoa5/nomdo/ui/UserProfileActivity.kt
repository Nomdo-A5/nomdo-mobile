package com.nomdoa5.nomdo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nomdoa5.nomdo.databinding.ActivityUserProfileBinding

class UserProfileActivity : AppCompatActivity() {
    private var _binding: ActivityUserProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}