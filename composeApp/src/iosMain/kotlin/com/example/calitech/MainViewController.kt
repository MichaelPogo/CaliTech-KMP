package com.example.calitech

import androidx.compose.ui.window.ComposeUIViewController
import com.example.calitech.di.commonModule
import com.example.calitech.di.platformModule
import org.koin.core.context.startKoin

/**
 * Initialize Koin for iOS.
 * Called from Swift AppDelegate or SwiftUI App init.
 */
fun initKoin() {
    startKoin {
        modules(commonModule, platformModule())
    }
}

/**
 * iOS entry point — creates the Compose UI view controller.
 */
fun MainViewController() = ComposeUIViewController { App() }