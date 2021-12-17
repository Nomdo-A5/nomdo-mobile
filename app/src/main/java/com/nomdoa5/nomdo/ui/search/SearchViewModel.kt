package com.nomdoa5.nomdo.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.repository.model.response.SearchResponse
import com.nomdoa5.nomdo.repository.remote.ApiService
import com.nomdoa5.nomdo.repository.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel : ViewModel() {
    private var _loadingState = MutableStateFlow<LoadingState>(LoadingState.Empty)
    val loadingState: StateFlow<LoadingState> = _loadingState
    private var _searchResult = MutableLiveData<SearchResponse>()
    val searchResult: LiveData<SearchResponse> = _searchResult

    fun search(token: String, query: String) {
        _loadingState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getSearch(token = "Bearer $token", query)


        requestCall.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                if (response.code() < 300) {
                    _searchResult.postValue(response.body()!!)
                    _loadingState.value = LoadingState.Success
                } else {
                    _loadingState.value = LoadingState.Error("Error ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                _loadingState.value = LoadingState.Error("onFailure Server")
            }
        })
    }
}