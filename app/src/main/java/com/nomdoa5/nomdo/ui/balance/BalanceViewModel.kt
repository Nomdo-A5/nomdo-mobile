package com.nomdoa5.nomdo.ui.balance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nomdoa5.nomdo.repository.model.Balance
import com.nomdoa5.nomdo.repository.model.request.balance.BalanceRequest
import com.nomdoa5.nomdo.repository.model.request.balance.UpdateBalanceRequest
import com.nomdoa5.nomdo.repository.model.response.balance.BalanceResponse
import com.nomdoa5.nomdo.repository.model.response.ReportResponse
import com.nomdoa5.nomdo.repository.remote.ApiService
import com.nomdoa5.nomdo.repository.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BalanceViewModel : ViewModel() {
    private val listBalance = MutableLiveData<ArrayList<Balance>>()
    private val setBalanceState = MutableLiveData<Boolean>()
    private val addBalanceState = MutableLiveData<Boolean>()
    private val updateBalanceState = MutableLiveData<Boolean>()
    private val deleteBalanceState = MutableLiveData<Boolean>()

    fun setAllBalance(token: String, idWorkspace: String) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getReport(token = "Bearer $token", idWorkspace)

        requestCall.enqueue(object : Callback<ReportResponse> {
            override fun onResponse(
                call: Call<ReportResponse>,
                response: Response<ReportResponse>
            ) {
                if (response.code().equals(200)) {
                    listBalance.postValue(response.body()!!.balance)
                    setBalanceState.postValue(true)
                } else if (response.code().equals(404)) {
                    setBalanceState.postValue(false)
                }
            }

            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                setBalanceState.postValue(false)
            }
        })
    }

    fun updateBalance(token: String, balance: UpdateBalanceRequest) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.updateBalance(token = "Bearer $token", balance)

        requestCall.enqueue(object : Callback<BalanceResponse> {
            override fun onResponse(
                call: Call<BalanceResponse>,
                response: Response<BalanceResponse>
            ) {
                updateBalanceState.value = true
            }

            override fun onFailure(call: Call<BalanceResponse>, t: Throwable) {
                updateBalanceState.value = false
            }
        })
    }

    fun deleteBalance(token: String, id: String) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.deleteBalance(token = "Bearer $token", id)

        requestCall.enqueue(object : Callback<BalanceResponse> {
            override fun onResponse(
                call: Call<BalanceResponse>,
                response: Response<BalanceResponse>
            ) {
                deleteBalanceState.postValue(true)
            }

            override fun onFailure(call: Call<BalanceResponse>, t: Throwable) {
                deleteBalanceState.postValue(false)
            }
        })
    }


    fun addBalance(token: String, newBalance: BalanceRequest) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.addBalance(token = "Bearer $token", newBalance)

        requestCall.enqueue(object : Callback<BalanceResponse> {
            override fun onResponse(
                call: Call<BalanceResponse>,
                response: Response<BalanceResponse>
            ) {
                addBalanceState.postValue(true)
            }

            override fun onFailure(call: Call<BalanceResponse>, t: Throwable) {
                addBalanceState.postValue(false)
            }
        })
    }

    fun getSetBalanceState(): LiveData<Boolean> {
        return setBalanceState
    }


    fun getBalance(): LiveData<ArrayList<Balance>> {
        return listBalance
    }

    fun getAddBalanceState(): LiveData<Boolean> {
        return addBalanceState
    }

    fun getUpdateBalanceState(): LiveData<Boolean> {
        return updateBalanceState
    }

    fun getDeleteBalanceState(): LiveData<Boolean> {
        return deleteBalanceState
    }
}