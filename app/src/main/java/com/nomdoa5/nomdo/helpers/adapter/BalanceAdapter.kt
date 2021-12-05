package com.nomdoa5.nomdo.helpers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.helpers.toCurrencyFormat
import com.nomdoa5.nomdo.repository.model.Balance
import com.nomdoa5.nomdo.repository.model.Board

class BalanceAdapter(var context: Context,  private val listener: OnBalanceClickListener) :
    RecyclerView.Adapter<BalanceAdapter.ViewHolder>() {
    private val mData = ArrayList<Balance>()
    private var limit: Int? = null

    fun setData(items: ArrayList<Balance>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    fun setLimit(count: Int){
        limit = count
    }

    interface OnBalanceClickListener {
        fun onBalanceClick(data: Balance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_balance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mData.size > 0) {
            val model = mData[position]
            if (model.isIncome == 1) {
                holder.tvNominal.setTextColor(ContextCompat.getColor(context, R.color.primary))
            } else {
                holder.tvNominal.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.accent_profile
                    )
                )
            }

            holder.tvDescription.text = model.balanceDescription
            holder.tvNominal.text = model.nominal!!.toDouble().toCurrencyFormat()
            holder.itemView.setOnClickListener{listener.onBalanceClick(mData[position])}
        } else {
            return
        }
    }

    override fun getItemCount(): Int {
        return if(limit != null){
            limit!!
        } else {
            mData.size
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDescription: TextView = itemView.findViewById(R.id.tv_description_balance)
        var tvDate: TextView = itemView.findViewById(R.id.tv_date_balance)
        var tvNominal: TextView = itemView.findViewById(R.id.tv_nominal_balance)
    }
}