package com.nomdoa5.nomdo.ui.balance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nomdoa5.nomdo.repository.model.request.ReportRequest
import com.nomdoa5.nomdo.repository.model.response.balance.ReportResponse
import com.nomdoa5.nomdo.repository.remote.ApiService
import com.nomdoa5.nomdo.repository.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MoneyReportViewModel : ViewModel() {
    private val listReport = MutableLiveData<ReportResponse>()
    private val setReportState = MutableLiveData<Boolean>()
    private val addBalanceState = MutableLiveData<Boolean>()
    private val updateBalanceState = MutableLiveData<Boolean>()
    private val deleteBalanceState = MutableLiveData<Boolean>()

    fun setMoneyReport(token: String, idReport: String) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getReport(token = "Bearer $token", idReport)

        requestCall.enqueue(object : Callback<ReportResponse> {
            override fun onResponse(
                call: Call<ReportResponse>,
                response: Response<ReportResponse>
            ) {
                if (response.code().equals(200)) {
                    listReport.postValue(response.body()!!)
                    setReportState.postValue(true)
                } else if (response.code().equals(404)) {
                    setReportState.postValue(false)
                }
            }

            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                setReportState.postValue(false)
            }
        })
    }

    fun addMoneyReport(token: String, newReport: ReportRequest) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.addReport(token = "Bearer $token", newReport)

        requestCall.enqueue(object : Callback<ReportResponse> {
            override fun onResponse(
                call: Call<ReportResponse>,
                response: Response<ReportResponse>
            ) {
                addBalanceState.postValue(true)
            }

            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                addBalanceState.postValue(false)
            }
        })
    }

    fun getMoneyReportState(): LiveData<Boolean> {
        return setReportState
    }

    fun getMoneyReport(): LiveData<ReportResponse> {
        return listReport
    }
}