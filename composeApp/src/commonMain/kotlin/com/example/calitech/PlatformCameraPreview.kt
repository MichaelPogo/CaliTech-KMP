package com.example.calitech

import androidx.compose.runtime.Composable
import com.example.calitech.platform.CameraFrame

/**
 * Expected composable function that provides the platform-specific camera preview.
 *
 * Android: CameraX PreviewView
 * iOS: AVCaptureSession (to be implemented)
 * Desktop: Webcam capture (to be implemented)
 */
@Composable
expect fun PlatformCameraPreview(
    isFrontCamera: Boolean,
    onFrameCaptured: (CameraFrame) -> Unit
)
