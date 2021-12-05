package com.nomdoa5.nomdo.ui.balance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nomdoa5.nomdo.repository.model.Balance
import com.nomdoa5.nomdo.repository.model.request.balance.BalanceRequest
import com.nomdoa5.nomdo.repository.model.request.balance.UpdateBalanceRequest
import com.nomdoa5.nomdo.repository.model.response.AddAttachmentResponse
import com.nomdoa5.nomdo.repository.model.response.balance.BalanceResponse
import com.nomdoa5.nomdo.repository.model.response.balance.CreateBalanceResponse
import com.nomdoa5.nomdo.repository.model.response.balance.ReportOverviewResponse
import com.nomdoa5.nomdo.repository.model.response.balance.ReportResponse
import com.nomdoa5.nomdo.repository.remote.ApiService
import com.nomdoa5.nomdo.repository.remote.RetrofitClient
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BalanceViewModel : ViewModel() {
    private val listBalance = MutableLiveData<ArrayList<Balance>>()
    private val listIncomeBalance = MutableLiveData<ArrayList<Balance>>()
    private val listOutcomeBalance = MutableLiveData<ArrayList<Balance>>()
    private val listIncomePlannedBalance = MutableLiveData<ArrayList<Balance>>()
    private val listOutcomePlannedBalance = MutableLiveData<ArrayList<Balance>>()
    private val listIncomeDoneBalance = MutableLiveData<ArrayList<Balance>>()
    private val listOutcomeDoneBalance = MutableLiveData<ArrayList<Balance>>()
    private val overviewBalance = MutableLiveData<ReportOverviewResponse>()
    private val overviewPlannedBalance = MutableLiveData<ReportOverviewResponse>()
    private val overviewDoneBalance = MutableLiveData<ReportOverviewResponse>()
    private val setBalanceState = MutableLiveData<Boolean>()
    private val addBalanceState = MutableLiveData<Boolean>()
    private val addBalanceResponse = MutableLiveData<Balance>()
    private val updateBalanceState = MutableLiveData<Boolean>()
    private val deleteBalanceState = MutableLiveData<Boolean>()

    fun setBalance(
        token: String,
        idWorkspace: String,
        isIncome: Int? = null,
        status: String? = null
    ) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getReport(token = "Bearer $token", idWorkspace, isIncome, status)

        requestCall.enqueue(object : Callback<ReportResponse> {
            override fun onResponse(
                call: Call<ReportResponse>,
                response: Response<ReportResponse>
            ) {
                val responses = response.body()!!.balance
                if (response.code() == 200) {
                    when {
                        isIncome == 1 && status == "Planned" -> {
                            listIncomePlannedBalance.postValue(responses)
                        }
                        isIncome == 0 && status == "Planned" -> {
                            listOutcomePlannedBalance.postValue(responses)
                        }
                        isIncome == 1 && status == "Done" -> {
                            listIncomeDoneBalance.postValue(responses)
                        }
                        isIncome == 0 && status == "Done" -> {
                            listOutcomeDoneBalance.postValue(responses)
                        }
                        isIncome == 1 -> {
                            listIncomeBalance.postValue(responses)
                        }
                        isIncome == 0 -> {
                            listOutcomeBalance.postValue(responses)
                        }
                        else -> {
                            listBalance.postValue(response.body()!!.balance)
                        }
                    }
                    setBalanceState.postValue(true)
                } else if (response.code() == 404) {
                    setBalanceState.postValue(false)
                }
            }

            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                setBalanceState.postValue(false)
            }
        })
    }

    fun setOverviewBalance(
        token: String,
        idWorkspace: String,
        status: String? = null
    ) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getOverviewReport(token = "Bearer $token", idWorkspace, status)

        requestCall.enqueue(object : Callback<ReportOverviewResponse> {
            override fun onResponse(
                call: Call<ReportOverviewResponse>,
                response: Response<ReportOverviewResponse>
            ) {
                val responses = response.body()!!

                if (response.code() == 200) {
                    when (status) {
                        "Planned" -> {
                            overviewPlannedBalance.postValue(responses)
                        }
                        "Done" -> {
                            overviewDoneBalance.postValue(responses)
                        }
                        else -> {
                            overviewBalance.postValue(responses)
                        }
                    }
                    setBalanceState.postValue(true)
                } else if (response.code() == 404) {
                    setBalanceState.postValue(false)
                }
            }

            override fun onFailure(call: Call<ReportOverviewResponse>, t: Throwable) {
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

        requestCall.enqueue(object : Callback<CreateBalanceResponse> {
            override fun onResponse(
                call: Call<CreateBalanceResponse>,
                response: Response<CreateBalanceResponse>
            ) {
                addBalanceState.postValue(true)
                addBalanceResponse.postValue(response.body()!!.balance)
            }

            override fun onFailure(call: Call<CreateBalanceResponse>, t: Throwable) {
                addBalanceState.postValue(false)
            }
        })
    }

    fun addAttachment(
        token: String,
        attachment: MultipartBody.Part,
        balanceId: RequestBody,
    ) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.addAttachment(token = "Bearer $token", attachment, balanceId)

        requestCall.enqueue(object : Callback<AddAttachmentResponse> {
            override fun onResponse(
                call: Call<AddAttachmentResponse>,
                response: Response<AddAttachmentResponse>
            ) {
                addBalanceState.postValue(true)
            }

            override fun onFailure(call: Call<AddAttachmentResponse>, t: Throwable) {
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

    fun getIncomeBalance(): LiveData<ArrayList<Balance>>{
        return listIncomeBalance
    }

    fun getOutcomeBalance(): LiveData<ArrayList<Balance>>{
        return listOutcomeBalance
    }

    fun getIncomePlannedBalance(): LiveData<ArrayList<Balance>> {
        return listIncomePlannedBalance
    }

    fun getOutcomePlannedBalance(): LiveData<ArrayList<Balance>> {
        return listOutcomePlannedBalance
    }

    fun getIncomeDoneBalance(): LiveData<ArrayList<Balance>> {
        return listIncomeDoneBalance
    }

    fun getOutcomeDoneBalance(): LiveData<ArrayList<Balance>> {
        return listOutcomeDoneBalance
    }

    fun getOverviewBalance(): LiveData<ReportOverviewResponse>{
        return overviewBalance
    }

    fun getOverviewPlannedBalance(): LiveData<ReportOverviewResponse>{
        return overviewPlannedBalance
    }

    fun getOverviewDoneBalance(): LiveData<ReportOverviewResponse>{
        return overviewDoneBalance
    }

    fun getAddBalanceState(): LiveData<Boolean> {
        return addBalanceState
    }

    fun getAddBalanceResponse(): LiveData<Balance> {
        return addBalanceResponse
    }

    fun getUpdateBalanceState(): LiveData<Boolean> {
        return updateBalanceState
    }

    fun getDeleteBalanceState(): LiveData<Boolean> {
        return deleteBalanceState
    }
}