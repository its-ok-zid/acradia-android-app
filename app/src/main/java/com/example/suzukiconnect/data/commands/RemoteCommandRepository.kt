package com.example.suzukiconnect.data.commands

import com.example.suzukiconnect.domain.model.RemoteCommand
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID

interface RemoteCommandRepository {
    /** Sends the command; returns once the backend has accepted it (HTTP 202). */
    suspend fun send(command: RemoteCommand): RemoteCommandAcceptedDto
}

class RemoteCommandRepositoryImpl(
    private val api: RemoteCommandApi,
    private val vin: String,
    private val requestedBy: String
) : RemoteCommandRepository {

    override suspend fun send(command: RemoteCommand): RemoteCommandAcceptedDto =
        api.submitCommand(
            vin = vin,
            // New id per tap, so a retried request isn't executed twice by the backend
            requestId = UUID.randomUUID().toString(),
            body = RemoteCommandRequest(
                command = command.name,
                source = SOURCE,
                requestedBy = requestedBy,
                timestamp = OffsetDateTime.now()
                    .truncatedTo(ChronoUnit.SECONDS)
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) // 2026-11-04T10:30:00+05:30
            )
        )

    private companion object {
        const val SOURCE = "MOBILE_APP"
    }
}