package com.example.suzukiconnect.domain.usecase

import com.example.suzukiconnect.domain.model.HomeData
import com.example.suzukiconnect.domain.repository.VehicleRepository

class GetHomeDataUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(): HomeData = repository.getHomeData()
}
