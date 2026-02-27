package com.example.calitech

import androidx.compose.runtime.Composable
import com.example.calitech.ui.viewmodel.MediaViewModel

/**
 * Expected composable for the platform-specific media screen.
 *
 * Each platform provides its own implementation for image/video picking
 * (Android uses Activity Result API, iOS uses UIImagePickerController, etc.)
 */
@Composable
expect fun PlatformMediaScreen(
    viewModel: MediaViewModel,
    onBack: () -> Unit
)
