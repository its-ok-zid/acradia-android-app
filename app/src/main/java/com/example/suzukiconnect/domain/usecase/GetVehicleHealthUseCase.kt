package com.example.suzukiconnect.domain.usecase

import com.example.suzukiconnect.domain.model.VehicleHealth
import com.example.suzukiconnect.domain.repository.VehicleRepository

class GetVehicleHealthUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(): VehicleHealth = repository.getVehicleHealth()
}
