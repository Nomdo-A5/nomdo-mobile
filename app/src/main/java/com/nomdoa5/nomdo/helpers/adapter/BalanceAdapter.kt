package com.nomdoa5.nomdo.helpers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.repository.model.BalanceModel
import java.text.DecimalFormat
import java.text.NumberFormat

class BalanceAdapter(var context: Context, var balance_list: List<BalanceModel>?) :
    RecyclerView.Adapter<BalanceAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_balance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (balance_list != null && balance_list!!.size > 0) {
            val model = balance_list!![position]
            holder.tv_description.text = model.keterangan

            val formatter = DecimalFormat("#,###")
            val formattedNumber = formatter.format(model.outcome.toDouble())

            holder.tv_nominal.text = formattedNumber

            if(model.income.equals("1")){
                holder.tv_type.text = "Income"
            }else{
                holder.tv_type.text = "Outcome"
            }
        } else {
            return
        }
    }

    override fun getItemCount(): Int {
        return balance_list!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_description: TextView
        var tv_type: TextView
        var tv_nominal: TextView

        init {
            tv_description = itemView.findViewById(R.id.tv_description_balance)
            tv_type = itemView.findViewById(R.id.tv_type_balance)
            tv_nominal = itemView.findViewById(R.id.tv_nominal_balance)
        }
    }
}