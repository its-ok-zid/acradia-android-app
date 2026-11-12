package com.example.suzukiconnect.data.source

import com.example.suzukiconnect.domain.model.HealthSystem
import com.example.suzukiconnect.domain.model.HomeData
import com.example.suzukiconnect.domain.model.Indicator
import com.example.suzukiconnect.domain.model.IndicatorType
import com.example.suzukiconnect.domain.model.VehicleHealth
import com.example.suzukiconnect.domain.model.VehicleStatus
import com.example.suzukiconnect.domain.model.NotificationScreenData

/** Hard-coded data that mirrors the provided UI screenshots. */
class StaticVehicleDataSource {

    fun homeData() = HomeData(
        lastActivity = "Last Activity: 15 hrs 25 mins ago",
        vehicleStatus = VehicleStatus(
            fuelBars = 11,
            totalFuelBars = 11,
            rangeKm = 405,
            odometerKm = 3971,
            indicators = listOf(
                Indicator(IndicatorType.AIR_CONDITIONER, false),
                Indicator(IndicatorType.HAZARD, false),
                Indicator(IndicatorType.HEADLAMP, false),
                Indicator(IndicatorType.BATTERY, true),
                Indicator(IndicatorType.DOOR_LOCK, true),
                Indicator(IndicatorType.DOOR, true)
            )
        ),
        statusMessage = "Car dashboard parameters are okay.",
        location = "Wing A, Sus Road, Sukhwani Panaroma"
    )

    fun vehicleHealth() = VehicleHealth(
        overallStatus = "Vehicle Health OK",
        statusDate = "Status as on 02 Nov 2026",
        systems = listOf(
            HealthSystem("AIRBAG SYSTEM", "OK"),
            HealthSystem("BRAKE SYSTEM", "OK"),
            HealthSystem("POWER TRAIN SYSTEM", "OK"),
            HealthSystem("STEERING SYSTEM", "OK"),
            HealthSystem("LV BATTERY STATUS", "WARNING"),
            HealthSystem("LV BATTERY HEALTH PERCENTAGE", "75.50%")
        )
    )

    /** Header shown on the Notification screen; the list itself comes from received push notifications. */
    fun notificationHeader() = NotificationScreenData(
        category = "Convenience",
        vehicleName = "GRAND VITARA - MBXX..73",
        notifications = emptyList()
    )
}
