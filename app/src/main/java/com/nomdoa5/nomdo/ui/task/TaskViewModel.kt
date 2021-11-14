package com.nomdoa5.nomdo.ui.task

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nomdoa5.nomdo.repository.model.Task
import com.nomdoa5.nomdo.repository.model.TaskProgress
import com.nomdoa5.nomdo.repository.model.Workspace
import com.nomdoa5.nomdo.repository.model.request.task.TaskRequest
import com.nomdoa5.nomdo.repository.model.request.task.UpdateTaskRequest
import com.nomdoa5.nomdo.repository.model.response.board.BoardResponse
import com.nomdoa5.nomdo.repository.model.response.task.CreateTaskResponse
import com.nomdoa5.nomdo.repository.model.response.task.TaskResponse
import com.nomdoa5.nomdo.repository.remote.ApiService
import com.nomdoa5.nomdo.repository.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskViewModel : ViewModel() {
    private val listTask = MutableLiveData<ArrayList<Task>>()
    private val listTaskProgress = MutableLiveData<ArrayList<TaskProgress>>()
    private val taskProgress = MutableLiveData<TaskProgress>()
    private val taskCount = MutableLiveData<Int>()
    private val taskDoneCount = MutableLiveData<Int>()
    private val detailTask = MutableLiveData<Task>()
    private val setTaskState = MutableLiveData<Boolean?>()
    private val addTaskState = MutableLiveData<Boolean>()
    private val updateTaskState = MutableLiveData<Boolean>()
    private val deleteTaskState = MutableLiveData<Boolean>()
    private val detailTaskState = MutableLiveData<Boolean>()

    fun setTask(token: String, idBoard: String) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getTask(token = "Bearer $token", idBoard)

        requestCall.enqueue(object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                val task = response.body()!!.task
                listTask.postValue(task)
                taskCount.postValue(task.count())
                var doneCount: Int = 0
                for (i in task) {
                    if (i.isDone == 1) {
                        doneCount++
                    }
                }
                taskProgress.postValue(TaskProgress(task.count(), doneCount))
                taskDoneCount.postValue(doneCount)
                setTaskState.postValue(true)
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                setTaskState.postValue(false)
            }
        })
    }

    fun setTaskProgress(token: String, idWorkspace: String) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getBoard(token = "Bearer $token", idWorkspace)
        val listIdBoard = ArrayList<String>()
        requestCall.enqueue(object : Callback<BoardResponse> {
            override fun onResponse(call: Call<BoardResponse>, response: Response<BoardResponse>) {
                val board = response.body()!!.board
                for (i in board) {
                    listIdBoard.add(i.id.toString())
                }
            }
            override fun onFailure(call: Call<BoardResponse>, t: Throwable) {
                setTaskState.postValue(false)
            }
        })

        val listResponse = ArrayList<TaskProgress>()
        for (i in listIdBoard) {
            Log.d("TASK INI LOOPING GES", i)
            val service = RetrofitClient.buildService(ApiService::class.java)
            val requestCall = service.getTask(token = "Bearer $token", i)

            requestCall.enqueue(object : Callback<TaskResponse> {
                override fun onResponse(
                    call: Call<TaskResponse>,
                    response: Response<TaskResponse>
                ) {
                    val task = response.body()!!.task
                    listTask.postValue(task)
                    taskCount.postValue(task.count())
                    var doneCount: Int = 0
                    for (i in task) {
                        if (i.isDone == 1) {
                            doneCount++
                        }
                    }
                    listResponse.add(TaskProgress(task.count(), doneCount))
                    setTaskState.postValue(true)
                }

                override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                    setTaskState.postValue(false)
                }
            })
        }
        listTaskProgress.postValue(listResponse)
    }

    fun addTask(token: String, newTask: TaskRequest) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.addTask(token = "Bearer $token", newTask)

        requestCall.enqueue(object : Callback<CreateTaskResponse> {
            override fun onResponse(
                call: Call<CreateTaskResponse>,
                response: Response<CreateTaskResponse>
            ) {
                addTaskState.postValue(true)
            }

            override fun onFailure(call: Call<CreateTaskResponse>, t: Throwable) {
                addTaskState.postValue(false)
            }
        })
    }

    fun updateTask(token: String, newTask: UpdateTaskRequest) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.updateTask(token = "Bearer $token", newTask)

        requestCall.enqueue(object : Callback<CreateTaskResponse> {
            override fun onResponse(
                call: Call<CreateTaskResponse>,
                response: Response<CreateTaskResponse>
            ) {
                updateTaskState.postValue(true)
            }

            override fun onFailure(call: Call<CreateTaskResponse>, t: Throwable) {
                updateTaskState.postValue(false)
            }
        })
    }

    fun deleteTask(token: String, id: String) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.deleteTask(token = "Bearer $token", id)

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

    fun getSetTaskState(): LiveData<Boolean?> {
        return setTaskState
    }


    fun getTask(): LiveData<ArrayList<Task>> {
        return listTask
    }

    fun getTaskCount(): LiveData<Int> {
        return taskCount
    }

    fun getTaskDoneCount(): LiveData<Int> {
        return taskDoneCount
    }

    fun getTaskProgress(): LiveData<TaskProgress> {
        return taskProgress
    }

    fun getListTaskProgress(): LiveData<ArrayList<TaskProgress>> {
        return listTaskProgress
    }

    fun getAddTaskState(): LiveData<Boolean> {
        return addTaskState
    }

    fun getUpdateTaskState(): LiveData<Boolean> {
        return updateTaskState
    }

    fun getDeleteTaskState(): LiveData<Boolean> {
        return deleteTaskState
    }

    fun getDetailTask(): LiveData<Task> {
        return detailTask
    }
}