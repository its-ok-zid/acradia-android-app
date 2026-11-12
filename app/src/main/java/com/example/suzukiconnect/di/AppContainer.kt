package com.example.suzukiconnect.di

import android.content.Context
import com.example.suzukiconnect.data.repository.VehicleRepositoryImpl
import com.example.suzukiconnect.data.source.StaticVehicleDataSource
import com.example.suzukiconnect.domain.repository.VehicleRepository
import com.example.suzukiconnect.domain.usecase.GetHomeDataUseCase
import com.example.suzukiconnect.domain.usecase.GetNotificationsUseCase
import com.example.suzukiconnect.domain.usecase.GetVehicleHealthUseCase
import com.example.suzukiconnect.BuildConfig
import com.example.suzukiconnect.data.commands.RemoteCommandApi
import com.example.suzukiconnect.data.commands.RemoteCommandRepository
import com.example.suzukiconnect.data.commands.RemoteCommandRepositoryImpl
import com.example.suzukiconnect.data.health.VehicleHealthApi
import com.example.suzukiconnect.data.notifications.NotificationStore
import com.example.suzukiconnect.data.trips.TripApi
import com.example.suzukiconnect.data.trips.TripRepository
import com.example.suzukiconnect.data.trips.TripRepositoryImpl
import com.example.suzukiconnect.presentation.home.HomeViewModel
import com.example.suzukiconnect.presentation.trips.TripsViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {
    private val dataSource = StaticVehicleDataSource()
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /** Push notifications saved on the device; written by the FCM service, read by the Notification screen. */
    val notificationStore = NotificationStore(context)

    private val vehicleHealthApi: VehicleHealthApi by lazy { retrofit.create(VehicleHealthApi::class.java) }
    private val repository: VehicleRepository = VehicleRepositoryImpl(
        dataSource, vehicleHealthApi, notificationStore, BuildConfig.VEHICLE_HEALTH_VIN
    )

    val getHomeDataUseCase = GetHomeDataUseCase(repository)
    val getVehicleHealthUseCase = GetVehicleHealthUseCase(repository)
    val getNotificationsUseCase = GetNotificationsUseCase(repository)

    private val remoteCommandApi: RemoteCommandApi by lazy { retrofit.create(RemoteCommandApi::class.java) }
    val remoteCommandRepository: RemoteCommandRepository by lazy {
        RemoteCommandRepositoryImpl(
            api = remoteCommandApi,
            vin = BuildConfig.VEHICLE_HEALTH_VIN,
            requestedBy = REQUESTED_BY
        )
    }
    fun createHomeViewModel() = HomeViewModel(getHomeDataUseCase, remoteCommandRepository)

    private val tripApi: TripApi by lazy {
        retrofit.create(TripApi::class.java)
    }
    val tripRepository: TripRepository by lazy { TripRepositoryImpl(tripApi) }
    fun createTripsViewModel() = TripsViewModel(tripRepository, BuildConfig.VEHICLE_HEALTH_VIN)

    private companion object {
        // TODO: replace with the signed-in user's id once login exists
        const val REQUESTED_BY = "user-1042"
    }
}