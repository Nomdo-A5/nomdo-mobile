package com.nomdoa5.nomdo.helpers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.repository.model.Balance
import com.nomdoa5.nomdo.repository.model.BalanceModel
import com.nomdoa5.nomdo.repository.model.Board
import com.nomdoa5.nomdo.repository.model.TaskProgress
import java.text.DecimalFormat
import java.text.NumberFormat

class BalanceAdapter(var context: Context) :
    RecyclerView.Adapter<BalanceAdapter.ViewHolder>() {

    private val mData = ArrayList<Balance>()

    fun setData(items: ArrayList<Balance>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_balance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mData.size > 0) {
            val model = mData[position]
            holder.tv_description.text = model.balanceDescription

            val formatter = DecimalFormat("#,###")
            val formattedNumber = formatter.format(model.nominal!!.toDouble())

            holder.tv_nominal.text = formattedNumber

            if (model.isIncome!!.equals(1)) {
                holder.tv_type.text = "Income"
            } else {
                holder.tv_type.text = "Outcome"
            }
        } else {
            return
        }
    }

    override fun getItemCount(): Int {
        return mData.size
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