package com.nomdoa5.nomdo.ui.workspace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nomdoa5.nomdo.repository.model.User
import com.nomdoa5.nomdo.repository.model.Workspace
import com.nomdoa5.nomdo.repository.model.request.workspace.UpdateWorkspaceRequest
import com.nomdoa5.nomdo.repository.model.request.workspace.WorkspaceRequest
import com.nomdoa5.nomdo.repository.model.response.workspace.*
import com.nomdoa5.nomdo.repository.remote.ApiService
import com.nomdoa5.nomdo.repository.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorkspacesViewModel : ViewModel() {
    private val listWorkspace = MutableLiveData<ArrayList<Workspace>>()
    private val detailWorkspace = MutableLiveData<Workspace>()
    private val memberWorkspace = MutableLiveData<ArrayList<User>>()
    private val createdWorkspace = MutableLiveData<Workspace>()
    private val taskInformationWorkspace = MutableLiveData<TaskInformationWorkspaceResponse>()
    private val workspaceState = MutableLiveData<Boolean>()
    private val addWorkspaceState = MutableLiveData<Boolean>()
    private val updateWorkspaceState = MutableLiveData<Boolean>()
    private val deleteWorkspaceState = MutableLiveData<Boolean>()

    fun setWorkspace(token: String) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getWorkspace(token = "Bearer $token")

        requestCall.enqueue(object : Callback<WorkspaceResponse> {

            override fun onResponse(
                call: Call<WorkspaceResponse>,
                response: Response<WorkspaceResponse>
            ) {
                if (!response.code().equals(500)) {
                    listWorkspace.postValue(response.body()!!.workspace)
                    workspaceState.postValue(true)
                }
            }

            override fun onFailure(call: Call<WorkspaceResponse>, t: Throwable) {
                workspaceState.postValue(false)
            }
        })
    }

    fun setDetailWorkspace(token: String, id: String) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getDetailWorkspace(token = "Bearer $token", id)

        requestCall.enqueue(object : Callback<DetailWorkspaceResponse> {
            override fun onResponse(
                call: Call<DetailWorkspaceResponse>,
                response: Response<DetailWorkspaceResponse>
            ) {
                if (!response.code().equals(500)) {
                    detailWorkspace.postValue(response.body()!!.workspace)
                    workspaceState.postValue(true)
                }
            }

            override fun onFailure(call: Call<DetailWorkspaceResponse>, t: Throwable) {
                workspaceState.postValue(false)
            }
        })
    }

    fun setMemberWorkspace(token: String, id: String) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getMemberWorkspace(token = "Bearer $token", id)

        requestCall.enqueue(object : Callback<MemberWorkspaceResponse> {
            override fun onResponse(
                call: Call<MemberWorkspaceResponse>,
                response: Response<MemberWorkspaceResponse>
            ) {
                if (!response.code().equals(500)) {
                    memberWorkspace.postValue(response.body()!!.member)
                    workspaceState.postValue(true)
                }
            }

            override fun onFailure(call: Call<MemberWorkspaceResponse>, t: Throwable) {
                workspaceState.postValue(false)
            }
        })
    }

    fun setTaskInfo(token: String, workspaceId: String) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getWorkspaceTaskInfo(token = "Bearer $token", workspaceId)

        requestCall.enqueue(object : Callback<TaskInformationWorkspaceResponse> {
            override fun onResponse(
                call: Call<TaskInformationWorkspaceResponse>,
                response: Response<TaskInformationWorkspaceResponse>
            ) {
                if (response.code() != 500) {
                    taskInformationWorkspace.postValue(response.body()!!)
                    workspaceState.postValue(true)
                }
            }

            override fun onFailure(call: Call<TaskInformationWorkspaceResponse>, t: Throwable) {
                workspaceState.postValue(false)
            }
        })
    }

    fun addWorkspace(token: String, newWorkspace: WorkspaceRequest) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.addWorkspace(token = "Bearer $token", newWorkspace)

        requestCall.enqueue(object : Callback<CreateWorkspaceResponse> {
            override fun onResponse(
                call: Call<CreateWorkspaceResponse>,
                response: Response<CreateWorkspaceResponse>
            ) {
                createdWorkspace.postValue(response.body()!!.workspace)
                addWorkspaceState.postValue(true)
            }

            override fun onFailure(call: Call<CreateWorkspaceResponse>, t: Throwable) {
                addWorkspaceState.postValue(false)
            }
        })
    }

    fun updateWorkspace(token: String, workspace: UpdateWorkspaceRequest) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.updateWorkspace(token = "Bearer $token", workspace)

        requestCall.enqueue(object : Callback<CreateWorkspaceResponse> {
            override fun onResponse(
                call: Call<CreateWorkspaceResponse>,
                response: Response<CreateWorkspaceResponse>
            ) {
                updateWorkspaceState.value = true
            }

            override fun onFailure(call: Call<CreateWorkspaceResponse>, t: Throwable) {
                updateWorkspaceState.value = false
            }
        })
    }

    fun deleteWorkspace(token: String, id: String) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.deleteWorkspace(token = "Bearer $token", id)

        requestCall.enqueue(object : Callback<WorkspaceResponse> {
            override fun onResponse(
                call: Call<WorkspaceResponse>,
                response: Response<WorkspaceResponse>
            ) {
                deleteWorkspaceState.value = true
            }

            override fun onFailure(call: Call<WorkspaceResponse>, t: Throwable) {
                deleteWorkspaceState.value = false
            }
        })
    }

    fun joinWorkspace(token: String, urlJoin: String, memberId: String) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.joinWorkspace(token = "Bearer $token", urlJoin, memberId)

        requestCall.enqueue(object : Callback<CreateWorkspaceResponse> {

            override fun onResponse(
                call: Call<CreateWorkspaceResponse>,
                response: Response<CreateWorkspaceResponse>
            ) {
                detailWorkspace.postValue(response.body()!!.workspace)
                workspaceState.postValue(true)
            }

            override fun onFailure(call: Call<CreateWorkspaceResponse>, t: Throwable) {
                workspaceState.postValue(false)
            }
        })
    }

    fun getWorkspaceState(): LiveData<Boolean> = workspaceState

    fun getAddWorkspaceState(): LiveData<Boolean> = addWorkspaceState

    fun getUpdateWorkspaceState(): LiveData<Boolean> = updateWorkspaceState

    fun getDeleteWorkspaceState(): LiveData<Boolean> = deleteWorkspaceState

    fun getWorkspace(): LiveData<ArrayList<Workspace>> = listWorkspace

    fun getWorkspaceDetail(): LiveData<Workspace> = detailWorkspace

    fun getTaskInfo(): LiveData<TaskInformationWorkspaceResponse> =
        taskInformationWorkspace

    fun getCreatedWorkspace(): LiveData<Workspace> = createdWorkspace

    fun getMemberWorkspace(): LiveData<ArrayList<User>> = memberWorkspace
}