package com.nomdoa5.nomdo.ui.balances;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.nomdoa5.nomdo.R;
import com.nomdoa5.nomdo.helpers.adapter.BalanceAdapter;
import com.nomdoa5.nomdo.databinding.ActivityMoneyReportBinding;
import com.nomdoa5.nomdo.repository.model.BalanceModel;
import com.nomdoa5.nomdo.ui.tasks.DetailTaskFragment;

import java.util.ArrayList;
import java.util.List;

public class MoneyReport extends AppCompatActivity implements View.OnClickListener {

    RecyclerView rv_balance;
    BalanceAdapter adapter;
    private ActivityMoneyReportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMoneyReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rv_balance = findViewById(R.id.rv_balance);

        setRecyclerView();

        binding.imgDownload.setOnClickListener(this);
        binding.imgEdit.setOnClickListener(this);
    }

    private void setRecyclerView(){
        rv_balance.setHasFixedSize(true);
        rv_balance.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BalanceAdapter(this, getList());
        rv_balance.setAdapter(adapter);
    }

    private List<BalanceModel> getList(){
        List<BalanceModel> balance_Model_list = new ArrayList<>();
        balance_Model_list.add(new BalanceModel("1", "Foto Copy", "0", "2000"));
        balance_Model_list.add(new BalanceModel("2", "Paid Promote", "500000", "0"));
        balance_Model_list.add(new BalanceModel("3", "Konsumsi", "0", "20000"));
        return balance_Model_list;
    }

    @Override
    public void onClick(View view) {
        if (binding.imgEdit.equals(view)) {
            Intent intent = new Intent(com.nomdoa5.nomdo.ui.balances.MoneyReport.this, DetailTaskFragment.class);
            startActivity(intent);
        }
        else if (binding.imgDownload.equals(view)){
            finish();
        }
    }
}