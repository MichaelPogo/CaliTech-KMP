package com.example.calitech.ui.navigation

/**
 * Sealed class representing navigation routes in the app.
 *
 * Uses sealed class pattern for type-safe navigation.
 */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Camera : Screen("camera")
    data object Media : Screen("media")
}
