package com.example.calitech.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calitech.ui.components.SkeletonOverlay
import com.example.calitech.ui.viewmodel.CameraViewModel

/**
 * Camera Screen — displays live camera preview with skeleton overlay.
 *
 * The camera preview itself is platform-specific (CameraX on Android,
 * AVFoundation on iOS). This composable provides the common overlay
 * layer and controls.
 *
 * @param viewModel CameraViewModel managing pose detection state
 * @param onBack Navigate back to home
 * @param cameraPreview Platform-specific camera preview composable slot
 */
@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    onBack: () -> Unit,
    cameraPreview: @Composable (
        isFrontCamera: Boolean,
        onFrameCaptured: (com.example.calitech.platform.CameraFrame) -> Unit
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Platform-specific camera preview (fills entire screen)
        cameraPreview(uiState.isFrontCamera) { frame ->
            viewModel.onFrameCaptured(frame)
        }

        // Skeleton overlay drawn on top of camera preview
        SkeletonOverlay(
            poseResult = uiState.poseResult,
            mirrorHorizontally = uiState.isFrontCamera,
            modifier = Modifier.fillMaxSize()
        )

        // Back button (top-left)
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(48.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = CircleShape
                )
        ) {
            Text(
                text = "←",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Camera flip button (top-right)
        IconButton(
            onClick = { viewModel.toggleCamera() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(48.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = CircleShape
                )
        ) {
            Text(
                text = "🔄",
                fontSize = 20.sp
            )
        }

        // Confidence indicator (bottom)
        if (uiState.poseResult.keypoints.isNotEmpty()) {
            Text(
                text = "Confidence: ${(uiState.poseResult.score * 100).toInt()}%",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Error display
        uiState.errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = CircleShape
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}
