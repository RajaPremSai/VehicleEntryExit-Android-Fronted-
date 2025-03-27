package com.example.vehicleentryexit

import com.example.vehicleentryexit.models.Announcement
import com.example.vehicleentryexit.models.AnnouncementDTO
import com.example.vehicleentryexit.models.Gate
import com.example.vehicleentryexit.models.GateDTO
import com.example.vehicleentryexit.models.Log
import com.example.vehicleentryexit.models.LogDTO
import com.example.vehicleentryexit.models.SecurityGuard
import com.example.vehicleentryexit.models.SecurityGuardDTO
import com.example.vehicleentryexit.models.UniversityVehicle
import com.example.vehicleentryexit.models.UniversityVehicleDTO
import com.example.vehicleentryexit.models.UserDTO
import com.example.vehicleentryexit.models.Vehicle
import com.example.vehicleentryexit.models.VehicleDTO
import com.example.vehicleentryexit.models.VehicleDetails
import com.example.vehicleentryexit.models.auth.LoginRequest
import com.example.vehicleentryexit.models.auth.LoginResponse
import com.example.vehicleentryexit.sg.SGProfileFragment
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("api/security-guard/vehicles/{vehicleNumber}")
    fun getVehicleDetails(@Path("vehicleNumber") vehicleNumber: String): Call<VehicleDetails>

    @GET("api/security-guard/gates")
    fun getGateNumbers(): Call<List<String>>

    @POST("api/security-guard/logs")
    fun postLog(@Body logDTO: LogDTO): Call<Log>

    @GET("api/security-guard/announcements")
    fun getAllAnnouncements(): Call<List<AnnouncementDTO>>

    @GET("api/security-guard/logs")
    fun getLogs(): Call<List<LogDTO>>

    @GET("api/security-guard/profile/email/{email}")
    fun getSecurityGuardByEmail(@Path("email") email: String): Call<SGProfileFragment.SecurityGuard>

    @GET("api/profile/email/{email}")
    fun getUserByEmail(@Path("email") email: String): Call<UserDTO>

    @GET("api/announcements")
    fun getAllAnnouncements1(): Call<List<AnnouncementDTO>>

    @GET("api/user-vehicle-logs/{empNumber}")
    fun getUserVehicleLogs(@Path("empNumber") empNumber: String): Call<List<Log>>

    @GET("api/vehicles/{empNumber}")
    fun getUserVehicles(@Path("empNumber") empNumber: String): Call<List<Vehicle>>

    @POST("api/add-vehicle")
    fun addVehicle(@Body vehicleDTO: VehicleDTO): Call<Vehicle>

    @DELETE("api/delete-vehicle/{vehicleNumber}")
    fun deleteVehicle(@Path("vehicleNumber") vehicleNumber: String): Call<Void>

    @PUT("api/edit-vehicle/{vehicleNumber}")
    fun editVehicle(
        @Path("vehicleNumber") vehicleNumber: String,
        @Body vehicleDTO: VehicleDTO
    ): Call<Vehicle>


    //Manager Security Guards
    @GET("api/manager/security-guards")
    fun getGuards(): Call<List<SecurityGuard>>

    @GET("api/manager/security-guards/{securityGuardId}")
    fun getSecurityGuard(@Path("securityGuardId") securityGuardId: String): Call<SecurityGuard>

    @POST("api/manager/security-guards")
    fun addSecurityGuard(@Body securityGuardDTO: SecurityGuardDTO): Call<SecurityGuard>

    @PUT("api/manager/security-guards/{securityGuardId}")
    fun updateSecurityGuard(
        @Path("securityGuardId") securityGuardId: String,
        @Body securityGuardDTO: SecurityGuardDTO
    ): Call<SecurityGuard>

    @DELETE("api/manager/{securityGuardId}")
    fun deleteSecurityGuard(@Path("securityGuardId") securityGuardId: String): Call<Void>

    @POST("api/manager/announcements")
    fun addAnnouncement(@Body announcementDTO: AnnouncementDTO): Call<Announcement>

    @DELETE("api/manager/announcements/{announcementId}")
    fun deleteAnnouncement(@Path("announcementId") announcementId: String): Call<Void>

    @GET("api/manager/announcements")
    fun getAnnouncements(): Call<List<Announcement>>

    @POST("api/manager/add-gates")
    fun addGates(@Body gateDTO: GateDTO): Call<Gate>

    @GET("api/manager/gates")
    fun getGates(): Call<List<Gate>>

    @DELETE("api/manager/gates/{gateNumber}")
    fun deleteGates(@Path("gateNumber") gateNumber: String): Call<Void>

    @GET("api/manager/university-vehicles")
    fun getUniversityVehicles(): Call<List<UniversityVehicle>>

    @POST("api/manager/add-university-vehicles")
    fun addUniversityVehicle(@Body vehicleDTO: UniversityVehicleDTO): Call<UniversityVehicle>

    @DELETE("api/manager/delete-university-vehicles/{vehicleNumber}")
    fun deleteUniversityVehicle(@Path("vehicleNumber") vehicleNumber: String): Call<Void>

    @PUT("api/manager/university-vehicles/{vehicleNumber}")
    fun editUniversityVehicle(
        @Path("vehicleNumber") vehicleNumber: String,
        @Body vehicleDTO: UniversityVehicleDTO
    ): Call<UniversityVehicle>
}