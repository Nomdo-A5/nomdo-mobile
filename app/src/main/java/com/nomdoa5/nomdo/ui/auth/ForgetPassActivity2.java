package com.nomdoa5.nomdo.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.nomdoa5.nomdo.databinding.ActivityForgetPass2Binding;

public class ForgetPassActivity2 extends AppCompatActivity implements View.OnClickListener {
    private ActivityForgetPass2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPass2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnConfirmCode.setOnClickListener(this);
        binding.imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (binding.btnConfirmCode.equals(view)) {
            Intent intent = new Intent(ForgetPassActivity2.this,ForgetPassActivity3.class);
            startActivity(intent);
        }
        else if (binding.imgBack.equals(view)){
            finish();
        }
    }
}