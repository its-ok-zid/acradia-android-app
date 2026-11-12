package com.example.suzukiconnect.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.suzukiconnect.di.AppContainer
import com.example.suzukiconnect.presentation.components.BottomTab
import com.example.suzukiconnect.presentation.health.VehicleHealthScreen
import com.example.suzukiconnect.presentation.health.VehicleHealthViewModel
import com.example.suzukiconnect.presentation.home.HomeScreen
import com.example.suzukiconnect.presentation.home.HomeViewModel
import com.example.suzukiconnect.presentation.notification.NotificationScreen
import com.example.suzukiconnect.presentation.notification.NotificationViewModel
import com.example.suzukiconnect.presentation.trips.TripsScreen

@Composable
fun AppNavHost(
    container: AppContainer,
    pendingRoute: String? = null,
    onPendingRouteHandled: () -> Unit = {},
    navController: NavHostController = rememberNavController()
) {
    // Opens a screen while keeping Home as the only screen below it in the back stack.
    val navigateTo: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(Screen.Home.route)
            launchSingleTop = true
        }
    }

    val onTabClick: (BottomTab) -> Unit = { tab ->
        when (tab) {
            BottomTab.HOME -> navController.popBackStack(Screen.Home.route, inclusive = false)
            BottomTab.VEHICLE_HEALTH -> navigateTo(Screen.VehicleHealth.route)
            BottomTab.NOTIFICATIONS -> navigateTo(Screen.Notification.route)
            BottomTab.TRIPS -> navigateTo(Screen.Trips.route)
            BottomTab.SERVICES -> Unit // not built yet
        }
    }

    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { container.createHomeViewModel() }
                }
            )
            HomeScreen(
                viewModel = viewModel,
                onVehicleHealthClick = { navigateTo(Screen.VehicleHealth.route) },
                onBottomTabClick = onTabClick
            )
        }

        composable(Screen.VehicleHealth.route) {
            val viewModel: VehicleHealthViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { VehicleHealthViewModel(container.getVehicleHealthUseCase) }
                }
            )
            VehicleHealthScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onBottomTabClick = onTabClick
            )
        }

        composable(Screen.Trips.route) {
            val viewModel: com.example.suzukiconnect.presentation.trips.TripsViewModel = viewModel(
                factory = viewModelFactory { initializer { container.createTripsViewModel() } }
            )
            TripsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onBottomTabClick = onTabClick
            )
        }

        composable(Screen.Notification.route) {
            val viewModel: NotificationViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { NotificationViewModel(container.getNotificationsUseCase) }
                }
            )
            NotificationScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onBottomTabClick = onTabClick
            )
        }
    }

    // Opens the screen requested by a tapped push notification
    LaunchedEffect(pendingRoute) {
        pendingRoute?.let { route ->
            if (route == Screen.VehicleHealth.route || route == Screen.Notification.route || route == Screen.Trips.route) {
                navigateTo(route)
            }
            onPendingRouteHandled()
        }
    }
}