package com.nomdoa5.nomdo.ui.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nomdoa5.nomdo.repository.model.Task
import com.nomdoa5.nomdo.repository.model.request.DeleteRequest
import com.nomdoa5.nomdo.repository.model.request.task.TaskRequest
import com.nomdoa5.nomdo.repository.model.request.task.UpdateTaskRequest
import com.nomdoa5.nomdo.repository.model.response.TaskResponse
import com.nomdoa5.nomdo.repository.remote.ApiService
import com.nomdoa5.nomdo.repository.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TasksViewModel : ViewModel() {
    private val listTask = MutableLiveData<ArrayList<Task>>()
    private val detailTask = MutableLiveData<Task>()
    private val setTaskState = MutableLiveData<Boolean>()
    private val addTaskState = MutableLiveData<Boolean>()
    private val updateTaskState = MutableLiveData<Boolean>()
    private val deleteTaskState = MutableLiveData<Boolean>()
    private val detailTaskState = MutableLiveData<Boolean>()

    fun setTask(token: String, idBoard: String) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getTask(token = "Bearer $token", idBoard)

        requestCall.enqueue(object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                listTask.postValue(response.body()!!.task)
                setTaskState.postValue(true)
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                setTaskState.postValue(false)
            }
        })
    }

    fun addTask(token: String, newTask: TaskRequest) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.addTask(token = "Bearer $token", newTask)

        requestCall.enqueue(object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                addTaskState.postValue(true)
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                addTaskState.postValue(false)
            }
        })
    }

    fun updateTask(token: String, newTask: UpdateTaskRequest) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.updateTask(token = "Bearer $token", newTask)

        requestCall.enqueue(object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                updateTaskState.postValue(true)
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                updateTaskState.postValue(false)
            }
        })
    }

    fun deleteTask(token: String, task: DeleteRequest) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.deleteTask(token = "Bearer $token", task)

        requestCall.enqueue(object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                deleteTaskState.postValue(true)
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                deleteTaskState.postValue(false)
            }
        })
    }

    fun setDetailTask(token: String, id: String) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getDetailTask(token = "Bearer $token", id)

        requestCall.enqueue(object : Callback<Task> {
            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                detailTask.postValue(response.body())
                detailTaskState.postValue(true)
            }

            override fun onFailure(call: Call<Task>, t: Throwable) {
                detailTaskState.postValue(false)
            }
        })
    }

    fun getSetTaskState(): LiveData<Boolean> {
        return setTaskState
    }


    fun getTask(): LiveData<ArrayList<Task>> {
        return listTask
    }

    fun getAddTaskState(): LiveData<Boolean> {
        return addTaskState
    }

    fun getUpdateTaskState(): LiveData<Boolean>{
        return updateTaskState
    }

    fun getDeleteTaskState(): LiveData<Boolean>{
        return deleteTaskState
    }

    fun getDetailTask(): LiveData<Task> {
        return detailTask
    }
}