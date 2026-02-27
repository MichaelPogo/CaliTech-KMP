package com.example.calitech

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.calitech.platform.CameraFrame

/**
 * iOS actual implementation for camera preview.
 *
 * Will use AVFoundation's AVCaptureSession via Kotlin/Native interop
 * once the SPM TFLite package and camera bridge are set up.
 */
@Composable
actual fun PlatformCameraPreview(
    isFrontCamera: Boolean,
    onFrameCaptured: (CameraFrame) -> Unit
) {
    // TODO: Implement AVFoundation camera preview via UIKit interop
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "iOS Camera Preview\n(Requires AVFoundation integration)",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
