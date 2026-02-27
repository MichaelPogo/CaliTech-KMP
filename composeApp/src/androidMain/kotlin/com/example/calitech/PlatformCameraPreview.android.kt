package com.example.calitech

import androidx.compose.runtime.Composable
import com.example.calitech.platform.AndroidCameraPreview
import com.example.calitech.platform.CameraFrame

/**
 * Android actual implementation — delegates to CameraX-based [AndroidCameraPreview].
 */
@Composable
actual fun PlatformCameraPreview(
    isFrontCamera: Boolean,
    onFrameCaptured: (CameraFrame) -> Unit
) {
    AndroidCameraPreview(
        isFrontCamera = isFrontCamera,
        onFrameCaptured = onFrameCaptured
    )
}
