package com.nomdoa5.nomdo.ui.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nomdoa5.nomdo.repository.model.Board
import com.nomdoa5.nomdo.repository.model.request.board.BoardRequest
import com.nomdoa5.nomdo.repository.model.request.board.UpdateBoardRequest
import com.nomdoa5.nomdo.repository.model.response.board.BoardResponse
import com.nomdoa5.nomdo.repository.model.response.board.CreateBoardResponse
import com.nomdoa5.nomdo.repository.remote.ApiService
import com.nomdoa5.nomdo.repository.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardViewModel : ViewModel() {
    private val listBoard = MutableLiveData<ArrayList<Board>>()
    private val setBoardState = MutableLiveData<Boolean>()
    private val addBoardState = MutableLiveData<Boolean>()
    private val updateBoardState = MutableLiveData<Boolean>()
    private val deleteBoardState = MutableLiveData<Boolean>()

    fun setBoard(token: String, idBoard: String) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getBoard(token = "Bearer $token", idBoard)

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

    fun updateBoard(token: String, board: UpdateBoardRequest) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.updateBoard(token = "Bearer $token", board)

        requestCall.enqueue(object : Callback<CreateBoardResponse> {
            override fun onResponse(call: Call<CreateBoardResponse>, response: Response<CreateBoardResponse>) {
                updateBoardState.value = true
            }

            override fun onFailure(call: Call<CreateBoardResponse>, t: Throwable) {
                updateBoardState.value = false
            }
        })
    }

    fun deleteBoard(token: String, id: String) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.deleteBoard(token = "Bearer $token", id)

        requestCall.enqueue(object : Callback<BoardResponse> {
            override fun onResponse(call: Call<BoardResponse>, response: Response<BoardResponse>) {
                deleteBoardState.postValue(true)
            }

            override fun onFailure(call: Call<BoardResponse>, t: Throwable) {
                deleteBoardState.postValue(false)
            }
        })
    }


    fun addBoard(token: String, newBoard: BoardRequest) {
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.addBoard(token = "Bearer $token", newBoard)

        requestCall.enqueue(object : Callback<CreateBoardResponse> {
            override fun onResponse(call: Call<CreateBoardResponse>, response: Response<CreateBoardResponse>) {
                addBoardState.postValue(true)
            }
            override fun onFailure(call: Call<CreateBoardResponse>, t: Throwable) {
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

    fun getUpdateBoardState(): LiveData<Boolean>{
        return updateBoardState
    }

    fun getDeleteBoardState(): LiveData<Boolean>{
        return deleteBoardState
    }
}