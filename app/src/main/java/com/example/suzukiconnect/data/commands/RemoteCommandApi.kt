package com.example.suzukiconnect.data.commands

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

data class RemoteCommandRequest(
    @SerializedName("command") val command: String,
    @SerializedName("source") val source: String,
    @SerializedName("requestedBy") val requestedBy: String,
    @SerializedName("params") val params: Map<String, Any> = emptyMap(),
    @SerializedName("timestamp") val timestamp: String
)

/** HTTP 202 Accepted body. */
data class RemoteCommandAcceptedDto(
    @SerializedName("commandId") val commandId: String,
    @SerializedName("vin") val vin: String,
    @SerializedName("status") val status: String,
    @SerializedName("acceptedAt") val acceptedAt: String?,
    @SerializedName("statusUrl") val statusUrl: String?
)

interface RemoteCommandApi {
    /** API 1 – Submit Remote Command. */
    @POST("api/v1/vehicles/{vin}/commands")
    suspend fun submitCommand(
        @Path("vin") vin: String,
        @Header("X-Request-Id") requestId: String,
        @Body body: RemoteCommandRequest
    ): RemoteCommandAcceptedDto
}