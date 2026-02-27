package com.example.calitech.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.calitech.domain.model.BodyPart
import com.example.calitech.domain.model.Keypoint
import com.example.calitech.domain.model.PoseResult
import com.example.calitech.domain.model.SkeletonConnections

/**
 * Reusable Composable that draws the human skeleton overlay on a Canvas.
 *
 * Renders keypoint dots and skeleton bone connections from a [PoseResult].
 * Coordinates are normalized (0..1) and scaled to the Canvas dimensions.
 *
 * @param poseResult The detected pose keypoints to render
 * @param modifier Modifier for the Canvas
 * @param keypointColor Color for keypoint dots
 * @param skeletonColor Color for skeleton lines
 * @param keypointRadius Radius for keypoint circles
 * @param lineWidth Width of skeleton connection lines
 * @param confidenceThreshold Minimum confidence to draw a keypoint/connection
 * @param mirrorHorizontally Whether to flip X coordinates (for front camera)
 */
@Composable
fun SkeletonOverlay(
    poseResult: PoseResult,
    modifier: Modifier = Modifier,
    keypointColor: Color = Color(0xFF00E676),
    skeletonColor: Color = Color(0xFF00BFA6),
    keypointRadius: Float = 8f,
    lineWidth: Float = 4f,
    confidenceThreshold: Float = 0.2f,
    mirrorHorizontally: Boolean = false
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        if (poseResult.keypoints.isEmpty()) return@Canvas

        val keypointMap = poseResult.keypoints.associateBy { it.bodyPart }

        // Draw skeleton connections (lines between keypoints)
        drawSkeletonLines(
            keypointMap = keypointMap,
            color = skeletonColor,
            strokeWidth = lineWidth,
            confidenceThreshold = confidenceThreshold,
            mirror = mirrorHorizontally
        )

        // Draw keypoint dots on top of lines
        drawKeypoints(
            keypoints = poseResult.keypoints,
            color = keypointColor,
            radius = keypointRadius,
            confidenceThreshold = confidenceThreshold,
            mirror = mirrorHorizontally
        )
    }
}

/**
 * Draw lines connecting adjacent keypoints to form the skeleton.
 */
private fun DrawScope.drawSkeletonLines(
    keypointMap: Map<BodyPart, Keypoint>,
    color: Color,
    strokeWidth: Float,
    confidenceThreshold: Float,
    mirror: Boolean
) {
    for ((from, to) in SkeletonConnections.connections) {
        val startKp = keypointMap[from] ?: continue
        val endKp = keypointMap[to] ?: continue

        if (startKp.confidence < confidenceThreshold ||
            endKp.confidence < confidenceThreshold
        ) continue

        val startOffset = keypointToOffset(startKp, mirror)
        val endOffset = keypointToOffset(endKp, mirror)

        drawLine(
            color = color,
            start = startOffset,
            end = endOffset,
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

/**
 * Draw circles at each detected keypoint location.
 */
private fun DrawScope.drawKeypoints(
    keypoints: List<Keypoint>,
    color: Color,
    radius: Float,
    confidenceThreshold: Float,
    mirror: Boolean
) {
    for (kp in keypoints) {
        if (kp.confidence < confidenceThreshold) continue

        val offset = keypointToOffset(kp, mirror)

        // Outer glow
        drawCircle(
            color = color.copy(alpha = 0.3f),
            radius = radius * 1.8f,
            center = offset
        )
        // Inner dot
        drawCircle(
            color = color,
            radius = radius,
            center = offset
        )
    }
}

/**
 * Convert normalized keypoint coordinates to canvas pixel offset.
 */
private fun DrawScope.keypointToOffset(
    keypoint: Keypoint,
    mirror: Boolean
): Offset {
    val x = if (mirror) 1f - keypoint.x else keypoint.x
    return Offset(
        x = x * size.width,
        y = keypoint.y * size.height
    )
}
