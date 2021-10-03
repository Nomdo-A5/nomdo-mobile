package com.nomdoa5.nomdo.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.nomdoa5.nomdo.databinding.ActivityForgetPassBinding;

public class ForgetPassActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityForgetPassBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSendCode.setOnClickListener(this);
        binding.imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (binding.btnSendCode.equals(view)) {
            Intent intent = new Intent(ForgetPassActivity.this,ForgetPassActivity2.class);
            startActivity(intent);
        }
        else if (binding.imgBack.equals(view)){
            finish();
        }
    }
}