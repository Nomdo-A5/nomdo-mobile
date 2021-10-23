package com.nomdoa5.nomdo.ui.boards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nomdoa5.nomdo.repository.model.Board
import com.nomdoa5.nomdo.repository.model.request.BoardRequest
import com.nomdoa5.nomdo.repository.model.response.BoardResponse
import com.nomdoa5.nomdo.repository.model.response.WorkspaceResponse
import com.nomdoa5.nomdo.repository.remote.ApiResponse
import com.nomdoa5.nomdo.repository.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardsViewModel : ViewModel() {
    private val listBoard = MutableLiveData<ArrayList<Board>>()
    private val setBoardState = MutableLiveData<Boolean>()
    private val addBoardState = MutableLiveData<Boolean>()

    fun setBoard(token: String, idWorkspace: String) {
        val service = RetrofitClient.buildService(ApiResponse::class.java)
        val requestCall = service.getBoard(token = "Bearer $token", idWorkspace)

        requestCall.enqueue(object : Callback<BoardResponse> {
            override fun onResponse(call: Call<BoardResponse>, response: Response<BoardResponse>) {
                if(response.code().equals(200)){
                    listBoard.postValue(response.body()!!.board)
                    setBoardState.postValue(true)
                }else if(response.code().equals(404)){
                    setBoardState.postValue(false)
                }
            }

            override fun onFailure(call: Call<BoardResponse>, t: Throwable) {
                setBoardState.postValue(false)
            }
        })
    }

    fun addBoard(token: String, newBoard: BoardRequest) {
        val service = RetrofitClient.buildService(ApiResponse::class.java)
        val requestCall = service.addBoard(token = "Bearer $token", newBoard)

        requestCall.enqueue(object : Callback<BoardResponse> {
            override fun onResponse(call: Call<BoardResponse>, response: Response<BoardResponse>) {
                addBoardState.postValue(true)
            }
            override fun onFailure(call: Call<BoardResponse>, t: Throwable) {
                addBoardState.postValue(false)
            }
        })
    }

    fun getSetBoardState(): LiveData<Boolean> {
        return setBoardState
    }


    fun getBoard(): LiveData<ArrayList<Board>> {
        return listBoard
    }

    fun getAddBoardState(): LiveData<Boolean> {
        return addBoardState
    }
}