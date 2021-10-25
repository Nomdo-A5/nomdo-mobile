package com.nomdoa5.nomdo.ui.workspaces

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nomdoa5.nomdo.repository.model.Workspace
import com.nomdoa5.nomdo.repository.model.request.workspace.DeleteWorkspaceRequest
import com.nomdoa5.nomdo.repository.model.request.workspace.UpdateWorkspaceRequest
import com.nomdoa5.nomdo.repository.model.request.workspace.WorkspaceRequest
import com.nomdoa5.nomdo.repository.model.response.WorkspaceResponse
import com.nomdoa5.nomdo.repository.remote.ApiService
import com.nomdoa5.nomdo.repository.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorkspacesViewModel : ViewModel() {
    private val listWorkspace = MutableLiveData<ArrayList<Workspace>>()
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
                listWorkspace.postValue(response.body()!!.workspace)
                workspaceState.postValue(true)
            }

            override fun onFailure(call: Call<WorkspaceResponse>, t: Throwable) {
                workspaceState.postValue(false)
            }
        })
    }

    fun addWorkspace(token: String, newWorkspace: WorkspaceRequest) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.addWorkspace(token = "Bearer $token", newWorkspace)

        requestCall.enqueue(object : Callback<WorkspaceResponse> {
            override fun onResponse(
                call: Call<WorkspaceResponse>,
                response: Response<WorkspaceResponse>
            ) {
                addWorkspaceState.postValue(true)
            }

            override fun onFailure(call: Call<WorkspaceResponse>, t: Throwable) {
                addWorkspaceState.postValue(false)
            }
        })
    }

    fun updateWorkspace(token: String, workspace: UpdateWorkspaceRequest) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.updateWorkspace(token = "Bearer $token", workspace)

        requestCall.enqueue(object : Callback<WorkspaceResponse> {
            override fun onResponse(
                call: Call<WorkspaceResponse>,
                response: Response<WorkspaceResponse>
            ) {
                updateWorkspaceState.value = true
            }

            override fun onFailure(call: Call<WorkspaceResponse>, t: Throwable) {
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

    fun getWorkspaceState(): LiveData<Boolean> {
        return workspaceState
    }

    fun getAddWorkspaceState(): LiveData<Boolean> {
        return addWorkspaceState
    }

    fun getUpdateWorkspaceState(): LiveData<Boolean> {
        return updateWorkspaceState
    }

    fun getDeleteWorkspaceState(): LiveData<Boolean> {
        return deleteWorkspaceState
    }

    fun getWorkspace(): LiveData<ArrayList<Workspace>> {
        return listWorkspace
    }

}