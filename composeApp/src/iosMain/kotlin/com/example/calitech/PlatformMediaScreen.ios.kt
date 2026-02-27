package com.example.calitech

import androidx.compose.runtime.Composable
import com.example.calitech.ui.screen.MediaScreen
import com.example.calitech.ui.viewmodel.MediaViewModel

/**
 * iOS actual implementation for the media screen.
 *
 * Image picking will use PHPickerViewController once the native
 * iOS bridge is set up.
 */
@Composable
actual fun PlatformMediaScreen(
    viewModel: MediaViewModel,
    onBack: () -> Unit
) {
    MediaScreen(
        viewModel = viewModel,
        onBack = onBack,
        onPickImage = {
            // TODO: Implement PHPickerViewController via Kotlin/Native interop
        },
        imageBitmap = null
    )
}
