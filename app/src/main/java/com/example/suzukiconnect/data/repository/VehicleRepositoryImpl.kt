package com.example.suzukiconnect.data.repository

import com.example.suzukiconnect.data.health.VehicleHealthApi
import com.example.suzukiconnect.data.source.StaticVehicleDataSource
import com.example.suzukiconnect.domain.model.HealthSystem
import com.example.suzukiconnect.domain.model.HomeData
import com.example.suzukiconnect.domain.model.NotificationScreenData
import com.example.suzukiconnect.domain.model.VehicleHealth
import com.example.suzukiconnect.domain.repository.VehicleRepository
import com.example.suzukiconnect.data.notifications.NotificationStore
import com.example.suzukiconnect.data.notifications.StoredNotification
import com.example.suzukiconnect.domain.model.NotificationItem
import com.example.suzukiconnect.domain.model.NotificationType
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VehicleRepositoryImpl(
    private val dataSource: StaticVehicleDataSource,
    private val vehicleHealthApi: VehicleHealthApi,
    private val notificationStore: NotificationStore,
    private val vin: String
) : VehicleRepository {
    override suspend fun getHomeData(): HomeData = dataSource.homeData()
    override suspend fun getVehicleHealth(): VehicleHealth {
        val dto = vehicleHealthApi.getVehicleHealth(vin)
        return VehicleHealth(
            overallStatus = "Vehicle Health",
            statusDate = "Last updated: ${dto.updatedAt}",
            systems = listOf(
                HealthSystem("AIRBAG SYSTEM", dto.airbagSystem),
                HealthSystem("BRAKE SYSTEM", dto.brakeSystem),
                HealthSystem("POWER TRAIN SYSTEM", dto.powerTrainSystem),
                HealthSystem("STEERING SYSTEM", dto.steeringSystem),
                HealthSystem("LV BATTERY STATUS", dto.lvBatteryStatus),
                HealthSystem("LV BATTERY HEALTH PERCENTAGE", "${"%.2f".format(java.util.Locale.US, dto.lvBatteryHealthPercent)}%")
            )
        )
    }
    override fun observeNotifications(): Flow<NotificationScreenData> {
        val header = dataSource.notificationHeader()
        return notificationStore.items.map { stored ->
            header.copy(notifications = stored.map { it.toDomain() })
        }
    }

    private fun StoredNotification.toDomain() = NotificationItem(
        id = id,
        type = runCatching { NotificationType.valueOf(type) }.getOrDefault(NotificationType.GENERAL),
        title = title,
        message = message,
        timestamp = Instant.ofEpochMilli(receivedAtMillis)
            .atZone(ZoneId.systemDefault())
            .format(TIMESTAMP_FORMAT) // "06 Nov 2026 at 02:08 AM"
    )

    private companion object {
        val TIMESTAMP_FORMAT: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy 'at' hh:mm a", Locale.ENGLISH)
    }
}
