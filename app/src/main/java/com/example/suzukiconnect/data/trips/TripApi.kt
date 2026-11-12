package com.example.suzukiconnect.data.trips

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class TripDto(
    @SerializedName("id") val id: String,
    @SerializedName("vin") val vin: String,
    @SerializedName("startAddress") val startAddress: String,
    @SerializedName("endAddress") val endAddress: String,
    @SerializedName("engineStartTime") val engineStartTime: String,
    @SerializedName("engineEndTime") val engineEndTime: String,
    @SerializedName("kmDriven") val kmDriven: Double,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class SaveTripRequest(
    val startAddress: String,
    val endAddress: String,
    val engineStartTime: String,
    val engineEndTime: String,
    val kmDriven: Double
)

interface TripApi {
    @GET("api/v1/vehicles/{vin}/trips")
    suspend fun getTrips(@Path("vin") vin: String): List<TripDto>

    @GET("api/v1/vehicles/{vin}/trips/{tripId}")
    suspend fun getTrip(@Path("vin") vin: String, @Path("tripId") tripId: String): TripDto

    @POST("api/v1/vehicles/{vin}/trips")
    suspend fun saveTrip(@Path("vin") vin: String, @Body body: SaveTripRequest): TripDto
}
