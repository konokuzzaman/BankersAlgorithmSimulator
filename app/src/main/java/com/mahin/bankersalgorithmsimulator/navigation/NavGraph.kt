package com.mahin.bankersalgorithmsimulator.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mahin.bankersalgorithmsimulator.ui.screens.HomeScreen
import com.mahin.bankersalgorithmsimulator.ui.screens.InputScreen
import com.mahin.bankersalgorithmsimulator.ui.screens.IntroductionScreen
import com.mahin.bankersalgorithmsimulator.ui.screens.RequestScreen
import com.mahin.bankersalgorithmsimulator.ui.screens.ResultScreen
import com.mahin.bankersalgorithmsimulator.viewmodel.SimulationViewModel

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    // Single shared instance, scoped to the Activity (the default owner here),
    // so Input and Result screens observe the exact same state.
    val simulationViewModel: SimulationViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToIntroduction = { navController.navigate(Screen.Introduction.route) },
                onNavigateToInput = { navController.navigate(Screen.Input.route) }
            )
        }
        composable(Screen.Introduction.route) {
            IntroductionScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.Input.route) {
            InputScreen(
                viewModel = simulationViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResult = { navController.navigate(Screen.Result.route) }
            )
        }
        composable(Screen.Result.route) {
            val state by simulationViewModel.state.collectAsStateWithLifecycle()
            ResultScreen(
                state = state,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRequest = { navController.navigate(Screen.Request.route) }
            )
        }
        composable(Screen.Request.route) {
            RequestScreen(
                viewModel = simulationViewModel,
            ) { navController.popBackStack() }
        }
    }
}