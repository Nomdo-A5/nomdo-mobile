package com.nomdoa5.nomdo.ui.balance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.repository.model.Balance
import com.nomdoa5.nomdo.repository.model.request.balance.BalanceRequest
import com.nomdoa5.nomdo.repository.model.request.balance.UpdateBalanceRequest
import com.nomdoa5.nomdo.repository.model.response.AddAttachmentResponse
import com.nomdoa5.nomdo.repository.model.response.MessageResponse
import com.nomdoa5.nomdo.repository.model.response.balance.BalanceResponse
import com.nomdoa5.nomdo.repository.model.response.balance.CreateBalanceResponse
import com.nomdoa5.nomdo.repository.model.response.balance.ReportOverviewResponse
import com.nomdoa5.nomdo.repository.model.response.balance.ReportResponse
import com.nomdoa5.nomdo.repository.remote.ApiService
import com.nomdoa5.nomdo.repository.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val addBalanceResponse = MutableLiveData<Balance>()
    private val _balanceState = MutableStateFlow<LoadingState>(LoadingState.Empty)
    val balanceState: StateFlow<LoadingState> = _balanceState

    fun setBalance(
        token: String,
        idWorkspace: String,
        isIncome: Int? = null,
        status: String? = null
    ) {
        _balanceState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getReport(token = "Bearer $token", idWorkspace, isIncome, status)

        requestCall.enqueue(object : Callback<ReportResponse> {
            override fun onResponse(
                call: Call<ReportResponse>,
                response: Response<ReportResponse>
            ) {
                if (response.code() < 300) {
                    val responses = response.body()!!.balance
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
                    _balanceState.value = LoadingState.Success
                } else {
                    _balanceState.value = LoadingState.Error("Error ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                _balanceState.value = LoadingState.Error("onFailure Server")
            }
        })
    }

    fun setOverviewBalance(
        token: String,
        idWorkspace: String,
        status: String? = null
    ) {
        _balanceState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getOverviewReport(token = "Bearer $token", idWorkspace, status)

        requestCall.enqueue(object : Callback<ReportOverviewResponse> {
            override fun onResponse(
                call: Call<ReportOverviewResponse>,
                response: Response<ReportOverviewResponse>
            ) {
                if (response.code() < 300) {
                    val responses = response.body()!!
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
                    _balanceState.value = LoadingState.Success
                } else {
                    _balanceState.value = LoadingState.Error("Error ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ReportOverviewResponse>, t: Throwable) {
                _balanceState.value = LoadingState.Error("onFailure Server")
            }
        })
    }

    fun updateBalance(token: String, balance: UpdateBalanceRequest) {
        _balanceState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.updateBalance(token = "Bearer $token", balance)

        requestCall.enqueue(object : Callback<BalanceResponse> {
            override fun onResponse(
                call: Call<BalanceResponse>,
                response: Response<BalanceResponse>
            ) {
                if (response.code() < 300) {
                    _balanceState.value = LoadingState.Success
                } else {
                    _balanceState.value = LoadingState.Error("Error ${response.code()}")
                }
            }

            override fun onFailure(call: Call<BalanceResponse>, t: Throwable) {
                _balanceState.value = LoadingState.Error("onFailure Server")
            }
        })
    }

    fun deleteBalance(token: String, id: String) {
        _balanceState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.deleteBalance(token = "Bearer $token", id)

        requestCall.enqueue(object : Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                if (response.code() < 300) {
                    _balanceState.value = LoadingState.Success
                } else {
                    _balanceState.value = LoadingState.Error("Error ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                _balanceState.value = LoadingState.Error("onFailure Server")
            }
        })
    }


    fun addBalance(token: String, newBalance: BalanceRequest) {
        _balanceState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.addBalance(token = "Bearer $token", newBalance)

        requestCall.enqueue(object : Callback<CreateBalanceResponse> {
            override fun onResponse(
                call: Call<CreateBalanceResponse>,
                response: Response<CreateBalanceResponse>
            ) {
                if (response.code() < 300) {
                    addBalanceResponse.postValue(response.body()!!.balance)
                    _balanceState.value = LoadingState.Success
                } else {
                    _balanceState.value = LoadingState.Error("Error ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CreateBalanceResponse>, t: Throwable) {
                _balanceState.value = LoadingState.Error("onFailure Server")
            }
        })
    }

    fun addAttachment(
        token: String,
        attachment: MultipartBody.Part,
        balanceId: RequestBody,
    ) {
        _balanceState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.addAttachment(token = "Bearer $token", attachment, balanceId)

        requestCall.enqueue(object : Callback<AddAttachmentResponse> {
            override fun onResponse(
                call: Call<AddAttachmentResponse>,
                response: Response<AddAttachmentResponse>
            ) {
                if (response.code() < 300) {
                    _balanceState.value = LoadingState.Success
                } else {
                    _balanceState.value = LoadingState.Error("Error ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AddAttachmentResponse>, t: Throwable) {
                _balanceState.value = LoadingState.Error("onFailure Server")
            }
        })
    }


    fun getBalance(): LiveData<ArrayList<Balance>> = listBalance

    fun getIncomeBalance(): LiveData<ArrayList<Balance>> = listIncomeBalance

    fun getOutcomeBalance(): LiveData<ArrayList<Balance>> = listOutcomeBalance

    fun getIncomePlannedBalance(): LiveData<ArrayList<Balance>> = listIncomePlannedBalance

    fun getOutcomePlannedBalance(): LiveData<ArrayList<Balance>> = listOutcomePlannedBalance

    fun getIncomeDoneBalance(): LiveData<ArrayList<Balance>> = listIncomeDoneBalance

    fun getOutcomeDoneBalance(): LiveData<ArrayList<Balance>> = listOutcomeDoneBalance

    fun getOverviewBalance(): LiveData<ReportOverviewResponse> = overviewBalance

    fun getOverviewPlannedBalance(): LiveData<ReportOverviewResponse> = overviewPlannedBalance

    fun getOverviewDoneBalance(): LiveData<ReportOverviewResponse> = overviewDoneBalance

    fun getAddBalanceResponse(): LiveData<Balance> = addBalanceResponse
}