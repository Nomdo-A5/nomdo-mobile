package com.nomdoa5.nomdo.ui.workspace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.repository.model.User
import com.nomdoa5.nomdo.repository.model.Workspace
import com.nomdoa5.nomdo.repository.model.request.workspace.UpdateWorkspaceRequest
import com.nomdoa5.nomdo.repository.model.request.workspace.WorkspaceRequest
import com.nomdoa5.nomdo.repository.model.response.workspace.*
import com.nomdoa5.nomdo.repository.remote.ApiService
import com.nomdoa5.nomdo.repository.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorkspacesViewModel : ViewModel() {
    private val listWorkspace = MutableLiveData<ArrayList<Workspace>>()
    private val countWorkspace = MutableLiveData<Int>()
    private val detailWorkspace = MutableLiveData<Workspace>()
    private val _workspaceState = MutableStateFlow<LoadingState>(LoadingState.Empty)
    val workspaceState: StateFlow<LoadingState> = _workspaceState
    private val memberWorkspace = MutableLiveData<ArrayList<User>>()
    private val createdWorkspace = MutableLiveData<Workspace>()
    private val taskInformationWorkspace = MutableLiveData<TaskInformationWorkspaceResponse>()

    fun setWorkspace(token: String) {
        _workspaceState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getWorkspace(token = "Bearer $token")

        requestCall.enqueue(object : Callback<WorkspaceResponse> {

            override fun onResponse(
                call: Call<WorkspaceResponse>,
                response: Response<WorkspaceResponse>
            ) {
                if (!response.code().equals(500)) {
                    listWorkspace.postValue(response.body()!!.workspace)
                    countWorkspace.postValue(response.body()!!.workspace.size)
                    _workspaceState.value = LoadingState.Success
                }
            }

            override fun onFailure(call: Call<WorkspaceResponse>, t: Throwable) {
                _workspaceState.value = LoadingState.Error("onFailure Server")
            }
        })
    }

    fun setDetailWorkspace(token: String, id: String) {
        _workspaceState.value = LoadingState.Success
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getDetailWorkspace(token = "Bearer $token", id)

        requestCall.enqueue(object : Callback<DetailWorkspaceResponse> {
            override fun onResponse(
                call: Call<DetailWorkspaceResponse>,
                response: Response<DetailWorkspaceResponse>
            ) {
                if (!response.code().equals(500)) {
                    detailWorkspace.postValue(response.body()!!.workspace)
                    _workspaceState.value = LoadingState.Success
                }
            }

            override fun onFailure(call: Call<DetailWorkspaceResponse>, t: Throwable) {
                _workspaceState.value = LoadingState.Error("onFailure Server")
            }
        })
    }

    fun setMemberWorkspace(token: String, id: String) {
        _workspaceState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getMemberWorkspace(token = "Bearer $token", id)

        requestCall.enqueue(object : Callback<MemberWorkspaceResponse> {
            override fun onResponse(
                call: Call<MemberWorkspaceResponse>,
                response: Response<MemberWorkspaceResponse>
            ) {
                if (!response.code().equals(500)) {
                    memberWorkspace.postValue(response.body()!!.member)
                    _workspaceState.value = LoadingState.Success
                }
            }

            override fun onFailure(call: Call<MemberWorkspaceResponse>, t: Throwable) {
                _workspaceState.value = LoadingState.Error("onFailure Server")
            }
        })
    }

    fun setTaskInfo(token: String, workspaceId: String) {
        _workspaceState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getWorkspaceTaskInfo(token = "Bearer $token", workspaceId)

        requestCall.enqueue(object : Callback<TaskInformationWorkspaceResponse> {
            override fun onResponse(
                call: Call<TaskInformationWorkspaceResponse>,
                response: Response<TaskInformationWorkspaceResponse>
            ) {
                if (response.code() != 500) {
                    taskInformationWorkspace.postValue(response.body()!!)
                    _workspaceState.value = LoadingState.Success
                }
            }

            override fun onFailure(call: Call<TaskInformationWorkspaceResponse>, t: Throwable) {
                _workspaceState.value = LoadingState.Error("onFailure Server")
            }
        })
    }

    fun addWorkspace(token: String, newWorkspace: WorkspaceRequest) {
        _workspaceState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.addWorkspace(token = "Bearer $token", newWorkspace)

        requestCall.enqueue(object : Callback<CreateWorkspaceResponse> {
            override fun onResponse(
                call: Call<CreateWorkspaceResponse>,
                response: Response<CreateWorkspaceResponse>
            ) {
                createdWorkspace.postValue(response.body()!!.workspace)
                _workspaceState.value = LoadingState.Success
            }
            override fun onFailure(call: Call<CreateWorkspaceResponse>, t: Throwable) {
                _workspaceState.value = LoadingState.Error("onFailure Server")
            }
        })
    }

    fun updateWorkspace(token: String, workspace: UpdateWorkspaceRequest) {
        _workspaceState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.updateWorkspace(token = "Bearer $token", workspace)

        requestCall.enqueue(object : Callback<CreateWorkspaceResponse> {
            override fun onResponse(
                call: Call<CreateWorkspaceResponse>,
                response: Response<CreateWorkspaceResponse>
            ) {
                _workspaceState.value = LoadingState.Success
            }

            override fun onFailure(call: Call<CreateWorkspaceResponse>, t: Throwable) {
                _workspaceState.value = LoadingState.Error("onFailure Server")
            }
        })
    }

    fun deleteWorkspace(token: String, id: String) {
        _workspaceState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.deleteWorkspace(token = "Bearer $token", id)

        requestCall.enqueue(object : Callback<WorkspaceResponse> {
            override fun onResponse(
                call: Call<WorkspaceResponse>,
                response: Response<WorkspaceResponse>
            ) {
                _workspaceState.value = LoadingState.Success
            }

            override fun onFailure(call: Call<WorkspaceResponse>, t: Throwable) {
                _workspaceState.value = LoadingState.Error("onFailure Server")
            }
        })
    }

    fun joinWorkspace(token: String, urlJoin: String, memberId: String) {
        _workspaceState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.joinWorkspace(token = "Bearer $token", urlJoin, memberId)

        requestCall.enqueue(object : Callback<CreateWorkspaceResponse> {

            override fun onResponse(
                call: Call<CreateWorkspaceResponse>,
                response: Response<CreateWorkspaceResponse>
            ) {
                detailWorkspace.postValue(response.body()!!.workspace)
                _workspaceState.value = LoadingState.Success
            }

            override fun onFailure(call: Call<CreateWorkspaceResponse>, t: Throwable) {
                _workspaceState.value = LoadingState.Error("onFailure Server")
            }
        })
    }

    fun getWorkspace(): LiveData<ArrayList<Workspace>> = listWorkspace

    fun getWorkspaceCount(): LiveData<Int> = countWorkspace

    fun getWorkspaceDetail(): LiveData<Workspace> = detailWorkspace

    fun getTaskInfo(): LiveData<TaskInformationWorkspaceResponse> =
        taskInformationWorkspace

    fun getCreatedWorkspace(): LiveData<Workspace> = createdWorkspace

    fun getMemberWorkspace(): LiveData<ArrayList<User>> = memberWorkspace
}