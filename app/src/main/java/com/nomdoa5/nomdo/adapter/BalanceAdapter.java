package com.nomdoa5.nomdo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nomdoa5.nomdo.R;
import com.nomdoa5.nomdo.model.BalanceModel;

import java.util.List;

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.ViewHolder> {

    Context context;
    List<BalanceModel> balance_list;

    public BalanceAdapter(Context context, List<BalanceModel> balance_list){
        this.context = context;
        this.balance_list = balance_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_balance,parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(balance_list != null && balance_list.size() > 0){
            BalanceModel model = balance_list.get(position);
            holder.tv_id.setText(model.getId());
            holder.tv_keterangan.setText(model.getKeterangan());
            holder.tv_income.setText(model.getIncome());
            holder.tv_outcome.setText(model.getOutcome());
        } else {
            return;
        }
    }

    @Override
    public int getItemCount() {
        return balance_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_id, tv_keterangan, tv_income, tv_outcome;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_id = itemView.findViewById(R.id.tv_id);
            tv_keterangan = itemView.findViewById(R.id.tv_keterangan);
            tv_income = itemView.findViewById(R.id.tv_income);
            tv_outcome = itemView.findViewById(R.id.tv_outcome);
        }
    }
}
