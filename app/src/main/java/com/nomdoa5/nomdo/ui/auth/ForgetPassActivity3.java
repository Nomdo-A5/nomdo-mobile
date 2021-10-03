package com.nomdoa5.nomdo.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.nomdoa5.nomdo.databinding.ActivityForgetPass3Binding;
import com.nomdoa5.nomdo.ui.MainActivity;

public class ForgetPassActivity3 extends AppCompatActivity implements View.OnClickListener {
    private ActivityForgetPass3Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPass3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnResetPassword.setOnClickListener(this);
        binding.imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (binding.btnResetPassword.equals(view)) {
            Intent intent = new Intent(ForgetPassActivity3.this, LoginActivity.class);
            startActivity(intent);
        }
        else if (binding.imgBack.equals(view)){
            finish();
        }
    }
}