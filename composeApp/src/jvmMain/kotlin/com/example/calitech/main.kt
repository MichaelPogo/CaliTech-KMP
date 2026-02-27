package com.example.calitech

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.calitech.di.commonModule
import com.example.calitech.di.platformModule
import org.koin.core.context.startKoin

/**
 * Desktop/JVM entry point.
 *
 * Initializes Koin and launches the Compose Desktop window.
 */
fun main() {
    startKoin {
        modules(commonModule, platformModule())
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "CaliTech — AI Pose Detection",
        ) {
            App()
        }
    }
}