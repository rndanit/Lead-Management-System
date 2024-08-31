
package com.rndtechnosoft.lms.Activity.Api

import com.rndtechnosoft.lms.Activity.DataModel.AddLeadRequest
import com.rndtechnosoft.lms.Activity.DataModel.AddLeadResponse
import com.rndtechnosoft.lms.Activity.DataModel.GetUserResponse
import com.rndtechnosoft.lms.Activity.DataModel.NotificationResponseItem
import com.rndtechnosoft.lms.Activity.DataModel.ShowLeadResponseItem
import com.rndtechnosoft.lms.Activity.DataModel.StatusResponse
import com.rndtechnosoft.lms.Activity.DataModel.UpdaredleadRequest
import com.rndtechnosoft.lms.Activity.DataModel.UpdatedFields
import com.rndtechnosoft.lms.Activity.DataModel.UpdatedLeadResponse
import com.rndtechnosoft.lms.Activity.DataModel.UserLoginRequest
import com.rndtechnosoft.lms.Activity.DataModel.UserLoginResponse
import com.rndtechnosoft.lms.Activity.DataModel.UserSignupRequest
import com.rndtechnosoft.lms.Activity.DataModel.UserSignupResponse
import com.rndtechnosoft.lms.Activity.DataModel.leadSourceResponseItem
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
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
    fun getUser(
        @Header("Authorization") token: String,
        @Query("id") id: String
    ): Call<GetUserResponse>

    //Update Profile EndPoint.
    @Multipart
    @PUT("user/updateUserById")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("mobile") mobile:RequestBody,
        @Part photo: MultipartBody.Part
    ): Call<UpdatedFields>



    //Notification EndPoint.
    @GET("notification/getNotification")
    fun notification(@Header("Authorization") token: String): Call<MutableList<NotificationResponseItem>>

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
}

