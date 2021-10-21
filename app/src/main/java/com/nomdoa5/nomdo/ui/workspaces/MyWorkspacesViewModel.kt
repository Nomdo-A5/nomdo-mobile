package com.nomdoa5.nomdo.ui.workspaces

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nomdoa5.nomdo.repository.model.Workspace
import com.nomdoa5.nomdo.repository.model.request.WorkspaceRequest
import com.nomdoa5.nomdo.repository.model.response.WorkspaceResponse
import com.nomdoa5.nomdo.repository.model.response.LoginResponse
import com.nomdoa5.nomdo.repository.remote.ApiResponse
import com.nomdoa5.nomdo.repository.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyWorkspacesViewModel : ViewModel() {
    private val listWorkspace = MutableLiveData<ArrayList<Workspace>>()
    private val workspaceState = MutableLiveData<Boolean>()
    private val addWorkspaceState = MutableLiveData<Boolean>()

    fun setWorkspace(token: String) {
        val service = RetrofitClient.buildService(ApiResponse::class.java)
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

    fun addWorkspace(token: String, newWorkspace: WorkspaceRequest){
        val service = RetrofitClient.buildService(ApiResponse::class.java)
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

    fun getWorkspaceState(): LiveData<Boolean> {
        return workspaceState
    }

    fun getAddWorkspaceState(): LiveData<Boolean> {
        return addWorkspaceState
    }

    fun getWorkspace(): LiveData<ArrayList<Workspace>>{
        return listWorkspace
    }
}