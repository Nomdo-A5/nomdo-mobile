package com.nomdoa5.nomdo.ui.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.repository.model.Board
import com.nomdoa5.nomdo.repository.model.Task
import com.nomdoa5.nomdo.repository.model.request.board.BoardRequest
import com.nomdoa5.nomdo.repository.model.request.board.UpdateBoardRequest
import com.nomdoa5.nomdo.repository.model.response.board.BoardResponse
import com.nomdoa5.nomdo.repository.model.response.board.CreateBoardResponse
import com.nomdoa5.nomdo.repository.model.response.board.TaskInformationBoardResponse
import com.nomdoa5.nomdo.repository.remote.ApiService
import com.nomdoa5.nomdo.repository.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardViewModel : ViewModel() {
    private val listBoard = MutableLiveData<ArrayList<Board>>()
    private val listTaskInformationBoard =
        MutableLiveData<ArrayList<TaskInformationBoardResponse>>()
    private val _boardState = MutableStateFlow<LoadingState>(LoadingState.Empty)
    val boardState: StateFlow<LoadingState> = _boardState

    fun setBoard(token: String, idBoard: String) {
        _boardState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.getBoard(token = "Bearer $token", idBoard)
        service.getTaskInformationBoard(token = "Bearer $token", idBoard)

        requestCall.enqueue(object : Callback<BoardResponse> {
            override fun onResponse(call: Call<BoardResponse>, response: Response<BoardResponse>) {
                if (response.code() < 300) {
                    listBoard.postValue(response.body()!!.boards)
                    setTaskInformationBoard(token, response.body()!!.boards)
                    _boardState.value = LoadingState.Success
                } else {
                    _boardState.value = LoadingState.Error("Error ${response.code()}")
                }
            }

            override fun onFailure(call: Call<BoardResponse>, t: Throwable) {
                _boardState.value = LoadingState.Error("onResponse Error")
            }
        })
    }

    fun setTaskInformationBoard(token: String, boards: ArrayList<Board>) {
        _boardState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val responses = ArrayList<TaskInformationBoardResponse>()
        for (board in boards) {
            val requestCall =
                service.getTaskInformationBoard(token = "Bearer $token", board.id.toString())

            requestCall.enqueue(object : Callback<TaskInformationBoardResponse> {
                override fun onResponse(
                    call: Call<TaskInformationBoardResponse>,
                    response: Response<TaskInformationBoardResponse>
                ) {
                    if (response.code() != 404 && response.code() != 500) {
                        responses.add(response.body()!!)
                        _boardState.value = LoadingState.Success
                    } else {
                        val info = TaskInformationBoardResponse(0, 0, "Empty task")
                        responses.add(info)
                    }
                    listTaskInformationBoard.postValue(responses)
                }

                override fun onFailure(call: Call<TaskInformationBoardResponse>, t: Throwable) {
                    _boardState.value = LoadingState.Error("onResponse Error")
                }
            })
        }
    }

    fun updateBoard(token: String, board: UpdateBoardRequest) {
        _boardState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.updateBoard(token = "Bearer $token", board)

        requestCall.enqueue(object : Callback<CreateBoardResponse> {
            override fun onResponse(
                call: Call<CreateBoardResponse>,
                response: Response<CreateBoardResponse>
            ) {
                if (response.code() < 300) {
                    _boardState.value = LoadingState.Success
                } else {
                    _boardState.value = LoadingState.Error("Error ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CreateBoardResponse>, t: Throwable) {
                _boardState.value = LoadingState.Error("onResponse Error")
            }
        })
    }

    fun deleteBoard(token: String, id: String) {
        _boardState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.deleteBoard(token = "Bearer $token", id)

        requestCall.enqueue(object : Callback<BoardResponse> {
            override fun onResponse(call: Call<BoardResponse>, response: Response<BoardResponse>) {
                if (response.code() < 300) {
                    _boardState.value = LoadingState.Success
                } else {
                    _boardState.value = LoadingState.Error("Error ${response.code()}")
                }
            }

            override fun onFailure(call: Call<BoardResponse>, t: Throwable) {
                _boardState.value = LoadingState.Error("onResponse Error")
            }
        })
    }


    fun addBoard(token: String, newBoard: BoardRequest) {
        _boardState.value = LoadingState.Loading
        val service = RetrofitClient.buildService(ApiService::class.java)
        val requestCall = service.addBoard(token = "Bearer $token", newBoard)

        requestCall.enqueue(object : Callback<CreateBoardResponse> {
            override fun onResponse(
                call: Call<CreateBoardResponse>,
                response: Response<CreateBoardResponse>
            ) {
                if (response.code() < 300) {
                    _boardState.value = LoadingState.Success
                } else {
                    _boardState.value = LoadingState.Error("Error ${response.code()}")
                }            }

            override fun onFailure(call: Call<CreateBoardResponse>, t: Throwable) {
                _boardState.value = LoadingState.Error("onResponse Error")
            }
        })
    }

    fun getBoard(): LiveData<ArrayList<Board>> {
        return listBoard
    }

    fun getTaskInformationBoard(): MutableLiveData<ArrayList<TaskInformationBoardResponse>> {
        return listTaskInformationBoard
    }
}