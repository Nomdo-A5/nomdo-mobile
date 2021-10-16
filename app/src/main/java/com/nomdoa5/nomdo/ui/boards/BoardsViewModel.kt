package com.nomdoa5.nomdo.ui.boards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nomdoa5.nomdo.repository.model.request.BoardRequest
import com.nomdoa5.nomdo.repository.model.response.WorkspaceResponse
import com.nomdoa5.nomdo.repository.remote.ApiResponse
import com.nomdoa5.nomdo.repository.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardsViewModel : ViewModel() {
    private val listBoard = MutableLiveData<BoardRequest>()
    private val boardState = MutableLiveData<Boolean>()

    fun setBoard(token: String, board: BoardRequest) {
        val service = RetrofitClient.buildService(ApiResponse::class.java)
        val requestCall = service.getBoard(token = "Bearer $token", board)

        requestCall.enqueue(object : Callback<BoardRequest> {

            override fun onResponse(call: Call<BoardRequest>, response: Response<BoardRequest>) {
                listBoard.postValue(response.body())
                boardState.postValue(true)
            }

            override fun onFailure(call: Call<BoardRequest>, t: Throwable) {
                boardState.postValue(false)
            }
        })
    }

    fun getWorkspaceState(): LiveData<Boolean> {
        return boardState
    }

    fun getWorkspace(): LiveData<BoardRequest> {
        return listBoard
    }
}