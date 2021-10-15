package com.nomdoa5.nomdo.ui.tasks

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nomdoa5.nomdo.model.Task
import com.nomdoa5.nomdo.repository.remote.ApiResponse
import com.nomdoa5.nomdo.repository.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TasksViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Task Fragment"
    }
    val text: LiveData<String> = _text

    fun setTask(){
        val service = RetrofitClient.buildService(ApiResponse::class.java)
        val requestCall = service.getTask()

        requestCall.enqueue(object : Callback<Task>{
            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                Log.d("ONREPSOSOSNTEUHOESNTUEH", "sotehunstoahusento")
            }
            override fun onFailure(call: Call<Task>, t: Throwable) {
                Log.d("GABISAAAAAAAAAAAAAAAAA", "WADUHhhhhhh")
            }
        })
    }
}