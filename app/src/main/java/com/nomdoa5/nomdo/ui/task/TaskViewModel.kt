package com.nomdoa5.nomdo.ui.task

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.repository.model.Task
import com.nomdoa5.nomdo.repository.model.request.task.TaskRequest
import com.nomdoa5.nomdo.repository.model.request.task.UpdateTaskRequest
import com.nomdoa5.nomdo.repository.model.response.task.CreateTaskResponse
import com.nomdoa5.nomdo.repository.model.response.task.TaskResponse
import com.nomdoa5.nomdo.repository.remote.ApiService
import com.nomdoa5.nomdo.repository.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskViewModel : ViewModel() {
    private val listTask = MutableLiveData<ArrayList<Task>>()
    private val detailTask = MutableLiveData<Task>()
    private val listDoneTask = MutableLiveData<ArrayList<Task>>()
    private val listTodayTask = MutableLiveData<ArrayList<Task>>()
    private val listWeekTask = MutableLiveData<ArrayList<Task>>()
    private val listLaterTask = MutableLiveData<ArrayList<Task>>()
    private val listOverdueTask = MutableLiveData<ArrayList<Task>>()
    private val _taskState = MutableStateFlow<LoadingState>(LoadingState.Empty)
    val taskState: StateFlow<LoadingState> = _taskState

    fun setTask(token: String, idBoard: String, isDone: Int? = null, dueDate: String? = null) {
        _taskState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getTask(token = "Bearer $token", idBoard, isDone, dueDate)

        requestCall.enqueue(object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {

                if (response.code() < 300) {
                    val task = response.body()!!.tasks
                    Log.d("TASK", task.toString())
                    if (isDone == 0) {
                        when (dueDate) {
                            "Today" -> listTodayTask.postValue(task)
                            "Overdue" -> listOverdueTask.postValue(task)
                            "Week" -> listWeekTask.postValue(task)
                            "Later" -> listLaterTask.postValue(task)
                        }
                    } else if (isDone == 1) {
                        listDoneTask.postValue(task)
                    } else {
                        listTask.postValue(task)
                    }
                    Log.d("TASKDONE", listDoneTask.value.toString())
                    _taskState.value = LoadingState.Success
                } else {
                    _taskState.value = LoadingState.Error("Error ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                _taskState.value = LoadingState.Error("onFailure Server")
            }
        })
    }

    fun addTask(token: String, newTask: TaskRequest) {
        _taskState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.addTask(token = "Bearer $token", newTask)

        requestCall.enqueue(object : Callback<CreateTaskResponse> {
            override fun onResponse(
                call: Call<CreateTaskResponse>,
                response: Response<CreateTaskResponse>
            ) {
                if (response.code() < 300) {
                    _taskState.value = LoadingState.Success
                } else {
                    _taskState.value = LoadingState.Error("Error ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CreateTaskResponse>, t: Throwable) {
                _taskState.value = LoadingState.Error("onFailure Server")
            }
        })
    }

    fun updateTask(token: String, newTask: UpdateTaskRequest) {
        _taskState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.updateTask(token = "Bearer $token", newTask)

        requestCall.enqueue(object : Callback<CreateTaskResponse> {
            override fun onResponse(
                call: Call<CreateTaskResponse>,
                response: Response<CreateTaskResponse>
            ) {
                if (response.code() < 300) {
                    _taskState.value = LoadingState.Success
                } else {
                    _taskState.value = LoadingState.Error("Error ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CreateTaskResponse>, t: Throwable) {
                _taskState.value = LoadingState.Error("onFailure Server")
            }
        })
    }

    fun deleteTask(token: String, id: String) {
        _taskState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.deleteTask(token = "Bearer $token", id)

        requestCall.enqueue(object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                if (response.code() < 300) {
                    _taskState.value = LoadingState.Success
                } else {
                    _taskState.value = LoadingState.Error("Error ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                _taskState.value = LoadingState.Error("onFailure Server")
            }
        })
    }

    fun setDetailTask(token: String, id: String) {
        _taskState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getDetailTask(token = "Bearer $token", id)

        requestCall.enqueue(object : Callback<Task> {
            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                if (response.code() < 300) {
                    detailTask.postValue(response.body())
                    _taskState.value = LoadingState.Success
                } else {
                    _taskState.value = LoadingState.Error("Error ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Task>, t: Throwable) {
                _taskState.value = LoadingState.Error("onFailure Server")
            }
        })
    }

    fun getTask(): LiveData<ArrayList<Task>> = listTask
    fun getDetailTask(): LiveData<Task> = detailTask
    fun getDoneTask(): LiveData<ArrayList<Task>> = listDoneTask
    fun getTodayTask(): LiveData<ArrayList<Task>> = listTodayTask
    fun getOverdueTask(): LiveData<ArrayList<Task>> = listOverdueTask
    fun getWeekTask(): LiveData<ArrayList<Task>> = listWeekTask
    fun getLaterTask(): LiveData<ArrayList<Task>> = listLaterTask
}