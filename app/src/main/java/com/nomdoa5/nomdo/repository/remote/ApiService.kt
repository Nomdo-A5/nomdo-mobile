package com.nomdoa5.nomdo.repository.remote

import com.nomdoa5.nomdo.repository.model.Task
import com.nomdoa5.nomdo.repository.model.request.ReportRequest
import com.nomdoa5.nomdo.repository.model.request.auth.LoginRequest
import com.nomdoa5.nomdo.repository.model.request.auth.RegisterRequest
import com.nomdoa5.nomdo.repository.model.request.balance.BalanceRequest
import com.nomdoa5.nomdo.repository.model.request.balance.UpdateBalanceRequest
import com.nomdoa5.nomdo.repository.model.request.board.BoardRequest
import com.nomdoa5.nomdo.repository.model.request.board.UpdateBoardRequest
import com.nomdoa5.nomdo.repository.model.request.task.TaskRequest
import com.nomdoa5.nomdo.repository.model.request.task.UpdateTaskRequest
import com.nomdoa5.nomdo.repository.model.request.workspace.UpdateWorkspaceRequest
import com.nomdoa5.nomdo.repository.model.request.workspace.WorkspaceRequest
import com.nomdoa5.nomdo.repository.model.response.*
import com.nomdoa5.nomdo.repository.model.response.auth.LoginResponse
import com.nomdoa5.nomdo.repository.model.response.auth.LogoutResponse
import com.nomdoa5.nomdo.repository.model.response.auth.RegisterResponse
import com.nomdoa5.nomdo.repository.model.response.balance.BalanceResponse
import com.nomdoa5.nomdo.repository.model.response.balance.CreateBalanceResponse
import com.nomdoa5.nomdo.repository.model.response.balance.ReportOverviewResponse
import com.nomdoa5.nomdo.repository.model.response.balance.ReportResponse
import com.nomdoa5.nomdo.repository.model.response.board.BoardResponse
import com.nomdoa5.nomdo.repository.model.response.board.CreateBoardResponse
import com.nomdoa5.nomdo.repository.model.response.board.TaskInformationBoardResponse
import com.nomdoa5.nomdo.repository.model.response.task.CreateTaskResponse
import com.nomdoa5.nomdo.repository.model.response.task.TaskResponse
import com.nomdoa5.nomdo.repository.model.response.workspace.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    //    AUTH
    @POST("login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Call<LoginResponse>

    @GET("user")
    fun getUser(
        @Header("Authorization") token: String
    ): Call<UserResponse>

    @POST("logout")
    fun logout(
        @Header("Authorization") token: String
    ): Call<LogoutResponse>

    @POST("register")
    fun register(
        @Body register: RegisterRequest
    ): Call<RegisterResponse>


    //     WORKSPACE
    @GET("workspace")
    fun getWorkspace(
        @Header("Authorization") token: String
    ): Call<WorkspaceResponse>

    @GET("workspace")
    fun getDetailWorkspace(
        @Header("Authorization") token: String,
        @Query("id") id: String,
    ): Call<DetailWorkspaceResponse>

    @GET("workspace/task-information")
    fun getWorkspaceTaskInfo(
        @Header("Authorization") token: String,
        @Query("workspace_id") workspaceId: String,
    ): Call<TaskInformationWorkspaceResponse>

    @POST("workspace")
    fun addWorkspace(
        @Header("Authorization") token: String,
        @Body workspace: WorkspaceRequest
    ): Call<CreateWorkspaceResponse>

    @PATCH("workspace")
    fun updateWorkspace(
        @Header("Authorization") token: String,
        @Body workspace: UpdateWorkspaceRequest
    ): Call<CreateWorkspaceResponse>

    @DELETE("workspace")
    fun deleteWorkspace(
        @Header("Authorization") token: String,
        @Query("id") id: String,
    ): Call<WorkspaceResponse>

    @GET("join")
    fun joinWorkspace(
        @Header("Authorization") token: String,
        @Query("url_join") urlJoin: String,
        @Query("member_id") memberId: String,
    ): Call<CreateWorkspaceResponse>

    @GET("workspace/member")
    fun getMemberWorkspace(
        @Header("Authorization") token: String,
        @Query("workspace_id") workspaceId: String,
    ): Call<MemberWorkspaceResponse>

    @GET("workspace/task-information")
    fun getTaskInformationWorkspace(
        @Header("Authorization") token: String,
        @Query("workspace_id") workspaceId: String,
    ): Call<TaskInformationWorkspaceResponse>


    //     BOARD
    @GET("boards")
    fun getBoard(
        @Header("Authorization") token: String,
        @Query("workspace_id") idWorkspace: String,
    ): Call<BoardResponse>

    @POST("boards")
    fun addBoard(
        @Header("Authorization") token: String,
        @Body board: BoardRequest
    ): Call<CreateBoardResponse>

    @PATCH("boards")
    fun updateBoard(
        @Header("Authorization") token: String,
        @Body board: UpdateBoardRequest
    ): Call<CreateBoardResponse>

    @DELETE("boards")
    fun deleteBoard(
        @Header("Authorization") token: String,
        @Query("id") id: String,
    ): Call<BoardResponse>

    @GET("boards/task-information")
    fun getTaskInformationBoard(
        @Header("Authorization") token: String,
        @Query("board_id") boardId: String,
    ): Call<TaskInformationBoardResponse>

    //    TASK
    @GET("task")
    fun getTask(
        @Header("Authorization") token: String,
        @Query("board_id") idBoard: String,
    ): Call<TaskResponse>

    @GET("task")
    fun getDetailTask(
        @Header("Authorization") token: String,
        @Query("id") id: String,
    ): Call<Task>

    @POST("task")
    fun addTask(
        @Header("Authorization") token: String,
        @Body task: TaskRequest
    ): Call<CreateTaskResponse>

    @PATCH("task")
    fun updateTask(
        @Header("Authorization") token: String,
        @Body task: UpdateTaskRequest
    ): Call<CreateTaskResponse>

    @DELETE("task")
    fun deleteTask(
        @Header("Authorization") token: String,
        @Query("id") id: String,
    ): Call<TaskResponse>


    //    MONEY REPORT
    @GET("report")
    fun getReport(
        @Header("Authorization") token: String,
        @Query("workspace_id") idWorkspace: String,
        @Query("is_income") isIncome: Int? = null,
        @Query("status") status: String? = null,
    ): Call<ReportResponse>

    @GET("report/overview")
    fun getOverviewReport(
        @Header("Authorization") token: String,
        @Query("workspace_id") idWorkspace: String,
        @Query("status") status: String? = null,
    ): Call<ReportOverviewResponse>

    @POST("report")
    fun addReport(
        @Header("Authorization") token: String,
        @Body report: ReportRequest,
    ): Call<ReportResponse>


    //    BALANCE
    @GET("balance")
    fun getBalance(
        @Header("Authorization") token: String,
        @Query("balance_id") idWorkspace: String,
    ): Call<BalanceResponse>

    @POST("balance")
    fun addBalance(
        @Header("Authorization") token: String,
        @Body task: BalanceRequest
    ): Call<CreateBalanceResponse>

    @PATCH("balance")
    fun updateBalance(
        @Header("Authorization") token: String,
        @Body balance: UpdateBalanceRequest
    ): Call<BalanceResponse>

    @DELETE("balance")
    fun deleteBalance(
        @Header("Authorization") token: String,
        @Query("id") id: String,
    ): Call<BalanceResponse>

    //    ATTACHMENT
    @Multipart
    @POST("attachment")
    fun addAttachment(
        @Header("Authorization") token: String,
        @Part file_path: MultipartBody.Part,
        @Part("balance_id") balanceId: RequestBody
    ): Call<AddAttachmentResponse>
}