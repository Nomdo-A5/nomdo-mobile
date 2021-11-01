package com.nomdoa5.nomdo.helpers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.repository.model.BalanceModel

class BalanceAdapter(var context: Context, var balance_list: List<BalanceModel>?) :
    RecyclerView.Adapter<BalanceAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_balance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (balance_list != null && balance_list!!.size > 0) {
            val model = balance_list!![position]
            holder.tv_id.text = model.id
            holder.tv_keterangan.text = model.keterangan
            holder.tv_income.text = model.income
            holder.tv_outcome.text = model.outcome
        } else {
            return
        }
    }

    override fun getItemCount(): Int {
        return balance_list!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_id: TextView
        var tv_keterangan: TextView
        var tv_income: TextView
        var tv_outcome: TextView

        init {
            tv_id = itemView.findViewById(R.id.tv_id)
            tv_keterangan = itemView.findViewById(R.id.tv_keterangan)
            tv_income = itemView.findViewById(R.id.tv_income)
            tv_outcome = itemView.findViewById(R.id.tv_outcome)
        }
    }
}