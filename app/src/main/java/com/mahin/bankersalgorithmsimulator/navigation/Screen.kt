package com.mahin.bankersalgorithmsimulator.navigation

/**
 * Sealed class enumerating every screen/route in the app.
 * Using a sealed class instead of raw strings prevents typos and gives
 * compile-time-checked navigation calls.
 */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Introduction : Screen("introduction")
    data object Input : Screen("input")
    data object Result : Screen("result")
    data object Request : Screen("request")
}