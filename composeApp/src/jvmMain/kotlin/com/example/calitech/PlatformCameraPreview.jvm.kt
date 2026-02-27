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
 * Desktop/JVM actual implementation for camera preview.
 *
 * Will use webcam-capture or OpenCV for desktop camera access.
 */
@Composable
actual fun PlatformCameraPreview(
    isFrontCamera: Boolean,
    onFrameCaptured: (CameraFrame) -> Unit
) {
    // TODO: Implement desktop camera capture via webcam-capture library
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Desktop Camera Preview\n(Requires webcam-capture integration)",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
