
package com.rndtechnosoft.lms.Activity.Api

import com.rndtechnosoft.lms.Activity.DataModel.AddLeadRequest
import com.rndtechnosoft.lms.Activity.DataModel.AddLeadResponse
import com.rndtechnosoft.lms.Activity.DataModel.AddNewLeadSourceRequest
import com.rndtechnosoft.lms.Activity.DataModel.AddNewLeadSourceResponse
import com.rndtechnosoft.lms.Activity.DataModel.AddStatusRequest
import com.rndtechnosoft.lms.Activity.DataModel.AddStatusResponse
import com.rndtechnosoft.lms.Activity.DataModel.DataXXX
import com.rndtechnosoft.lms.Activity.DataModel.DeleteLeadResponse
import com.rndtechnosoft.lms.Activity.DataModel.EditStatusRequest
import com.rndtechnosoft.lms.Activity.DataModel.EditStatusResponse
import com.rndtechnosoft.lms.Activity.DataModel.ForgetRequest
import com.rndtechnosoft.lms.Activity.DataModel.ForgetResponse
import com.rndtechnosoft.lms.Activity.DataModel.GetAllLeadsResponse
import com.rndtechnosoft.lms.Activity.DataModel.GetUserResponse
import com.rndtechnosoft.lms.Activity.DataModel.LeadEditRequest
import com.rndtechnosoft.lms.Activity.DataModel.LeadSourcesResponse
import com.rndtechnosoft.lms.Activity.DataModel.ManagerResponse
import com.rndtechnosoft.lms.Activity.DataModel.NotificationResponseItem
import com.rndtechnosoft.lms.Activity.DataModel.RestRequest
import com.rndtechnosoft.lms.Activity.DataModel.RestResponse
import com.rndtechnosoft.lms.Activity.DataModel.ShowLeadResponseItem
import com.rndtechnosoft.lms.Activity.DataModel.StatusResponse
import com.rndtechnosoft.lms.Activity.DataModel.StatusTypeResponse
import com.rndtechnosoft.lms.Activity.DataModel.UpdaredleadRequest
import com.rndtechnosoft.lms.Activity.DataModel.UpdatedFields
import com.rndtechnosoft.lms.Activity.DataModel.UpdatedLeadResponse
import com.rndtechnosoft.lms.Activity.DataModel.UpdatedLeadSource
import com.rndtechnosoft.lms.Activity.DataModel.UserLoginRequest
import com.rndtechnosoft.lms.Activity.DataModel.UserLoginResponse
import com.rndtechnosoft.lms.Activity.DataModel.UserSignupRequest
import com.rndtechnosoft.lms.Activity.DataModel.UserSignupResponse
import com.rndtechnosoft.lms.Activity.DataModel.emailRequest
import com.rndtechnosoft.lms.Activity.DataModel.leadSourceResponseItem
import com.rndtechnosoft.lms.Activity.DataModel.nameRequest
import com.rndtechnosoft.lms.Activity.DataModel.numberRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiInterface {

    //Login Endpoint and Network call.
    @POST("user/login")
    fun loginUser(@Body request: UserLoginRequest): Call<UserLoginResponse>

    //Signup Endpoint and Network call.
    @POST("user/register")
    fun signupUser(@Body request: UserSignupRequest): Call<UserSignupResponse>

    //Add Lead End Point
    @POST("lead/createLead")
    fun addLead(
        @Header("Authorization") token: String,
        @Query("id") id: String,
        @Body request: AddLeadRequest
    ): Call<AddLeadResponse>

    //Show Lead End Point
    @GET("lead/getLeadByLeadtype")
    fun showLead(
        @Header("Authorization") token: String,
        @Query("user") user: String
    ): Call<List<ShowLeadResponseItem>>


    //GetUserById Endpoint.
    @GET("user/getUserByUserId")
    fun getUser(@Header("Authorization") token: String, @Query("id") id: String): Call<GetUserResponse>

    //GetManagerById Endpoint.
    @GET("manager/getManagerById")
    fun getManager(@Header("Authorization") token: String, @Query("id") id: String): Call<ManagerResponse>

    //Update Profile EndPoint.
    @Multipart
    @PUT("user/updateUserById")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("mobile") mobile:RequestBody,
        @Part("password")password:RequestBody,
        @Part("companyname")companyname:RequestBody,
        @Part("website")website:RequestBody,
        @Part photo: MultipartBody.Part,
        @Query("id") id:String,
    ): Call<UpdatedFields>

    @Multipart
    @PUT("user/updateUserById")
    fun updateProfileData(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("mobile") mobile:RequestBody,
        @Part("password")password:RequestBody,
        @Part("companyname")companyname:RequestBody,
        @Part("website")website:RequestBody,
        @Query("id") id:String,
    ): Call<UpdatedFields>

    //Manager Update profile Endpoint.
    @Multipart
    @PUT("manager/updateManager")
    fun updateProfileManager(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("mobile") mobile:RequestBody,
        @Part("password")password:RequestBody,
        @Part("companyname")companyname:RequestBody,
        @Part("website")website:RequestBody,
        @Part photo: MultipartBody.Part,
        @Query("id") id:String,

    ): Call<UpdatedFields>

    @Multipart
    @PUT("manager/updateManager")
    fun updateProfileManagerData(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("mobile") mobile:RequestBody,
        @Part("password")password:RequestBody,
        @Part("companyname")companyname:RequestBody,
        @Part("website")website:RequestBody,
        @Query("id") id:String,

        ): Call<UpdatedFields>


    //Notification EndPoint.
    @GET("notification/getNotification")
    fun notification(@Header("Authorization") token: String, @Query("id") id: String): Call<MutableList<NotificationResponseItem>>

    //Notification AddLead or CancelLead EndPoint.
    @PUT("notification/updateIsExcept")
    fun notificationOperations(@Header("Authorization") token: String, @Query("id") id: String,@Query("isExcept") isExcept:Any ): Call<Void>

    //Status Endpoint.
    @GET("lead/filterUserLeadsByStatus")
    fun statusOperation(@Header("Authorization") token: String, @Query("user") user: String,@Query("status") status:String): Call<StatusResponse>

    //Status Updated.
    @PUT("lead/updatelead")
    fun statusUpdate(@Header("Authorization") token: String,@Query("id") id: String,@Body request: UpdaredleadRequest): Call<UpdatedLeadResponse>

    //Lead Source Item Endpoint.
    @GET("leadSource/getAllLeadSources")
    fun leadSource(@Header("Authorization")  token: String) :Call<MutableList<leadSourceResponseItem>>

    //StatusType EndPoint.
    @GET("statusType/getAllStatus")
    fun statusType(@Header("Authorization") token: String,@Query("userId") userId:String) :Call<MutableList<DataXXX>>

    //status card
    @GET("statusType/getStatusTypeByUser")
    fun statusCard(@Header("Authorization") token: String,@Query("userId") userId:String) :Call<StatusTypeResponse>

    //Forget Password Endpoint.
    @POST("user/forgotPassword")
    fun forgetFunction(@Body request: ForgetRequest) :Call<ForgetResponse>

    //Rest Password Endpoint.
    @POST("user/resetPassword")
    fun restFunction(@Body request: RestRequest):Call<RestResponse>

    //Add Status Api Endpoint.
    @POST("statusType/adduserStatusAdmin")
    fun addFunction(@Header("Authorization") token: String,@Query("user") user:String,@Body request: AddStatusRequest): Call<AddStatusResponse>

    //Edit Status Endpoint.
    @PUT("statusType/updateUserStatusType")
    fun editStatus(@Header("Authorization") token: String,@Query("id") id:String,@Body request: EditStatusRequest ):Call<EditStatusResponse>

    //Lead Source Fetch Endpoint.
    @GET("userLeadSource/getLeadSource")
    fun LeadSourceFunction(@Header("Authorization") token: String,@Query("userId") userId:String):Call<LeadSourcesResponse>

    //Lead Source Edit Endpoint.
    @PUT("userLeadSource/updateUserLeadSource")
    fun LeadSourcesEdit(@Header("Authorization") token: String,@Query("id") id:String,@Body request:LeadEditRequest) :Call<UpdatedLeadSource>

    //Lead Source Delete Endpoint.
    @DELETE("userLeadSource/deleteUserLeadSource")
    fun DeleteLeads(@Header("Authorization") token: String,@Query("id") id:String) : Call<DeleteLeadResponse>

    //Lead Source Add Endpoint.
    @POST("userLeadSource/addLeadSource")
    fun AddNewLeadSource(@Header("Authorization") token: String,@Query("user") user:String,@Body request: AddNewLeadSourceRequest) :Call<AddNewLeadSourceResponse>


    //All Leads show Endpoint.
    @GET("lead/getLeadById")
    fun GetAllLeads(@Header("Authorization") token: String,@Query("userId") userId:String) :Call<GetAllLeadsResponse>

}

