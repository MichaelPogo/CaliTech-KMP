package com.example.calitech

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calitech.ui.navigation.Screen
import com.example.calitech.ui.screen.CameraScreen
import com.example.calitech.ui.screen.HomeScreen
import com.example.calitech.ui.screen.MediaScreen
import com.example.calitech.ui.theme.CaliTechTheme
import com.example.calitech.ui.viewmodel.CameraViewModel
import com.example.calitech.ui.viewmodel.MediaViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Root composable for the CaliTech app.
 *
 * Sets up the Material3 theme, NavHost with 3 routes, and
 * injects ViewModels via Koin Compose integration.
 */
@Composable
fun App() {
    CaliTechTheme(darkTheme = true) {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            // Home Screen
            composable(Screen.Home.route) {
                HomeScreen(
                    onOpenCamera = {
                        navController.navigate(Screen.Camera.route)
                    },
                    onOpenMedia = {
                        navController.navigate(Screen.Media.route)
                    }
                )
            }

            // Live Camera Screen
            composable(Screen.Camera.route) {
                val viewModel = koinViewModel<CameraViewModel>()
                CameraScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    cameraPreview = { isFrontCamera, onFrame ->
                        PlatformCameraPreview(
                            isFrontCamera = isFrontCamera,
                            onFrameCaptured = onFrame
                        )
                    }
                )
            }

            // Media Analysis Screen
            composable(Screen.Media.route) {
                val viewModel = koinViewModel<MediaViewModel>()
                PlatformMediaScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}