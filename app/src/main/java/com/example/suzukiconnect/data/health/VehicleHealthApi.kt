package com.example.suzukiconnect.data.health

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path

data class VehicleHealthDto(
    @SerializedName("id") val id: String,
    @SerializedName("vin") val vin: String,
    @SerializedName("airbagSystem") val airbagSystem: String,
    @SerializedName("brakeSystem") val brakeSystem: String,
    @SerializedName("powerTrainSystem") val powerTrainSystem: String,
    @SerializedName("steeringSystem") val steeringSystem: String,
    @SerializedName("lvBatteryStatus") val lvBatteryStatus: String,
    @SerializedName("lvBatteryHealthPercent") val lvBatteryHealthPercent: Double,
    @SerializedName("updatedAt") val updatedAt: String
)

interface VehicleHealthApi {
    @GET("api/v1/vehicles/{vin}/health")
    suspend fun getVehicleHealth(@Path("vin") vin: String): VehicleHealthDto
}
