package com.example.calitech.domain.usecase

import com.example.calitech.domain.detector.PoseDetector
import com.example.calitech.domain.model.PoseResult

/**
 * Use case that orchestrates pose estimation on an image frame.
 *
 * Adheres to the Single Responsibility Principle — this class only
 * coordinates between the caller and the [PoseDetector].
 *
 * @property poseDetector The platform-specific pose detector injected via Koin
 */
class PoseEstimationUseCase(
    private val poseDetector: PoseDetector
) {
    /**
     * Runs pose estimation on a single image frame.
     *
     * @param imageBytes Raw pixel data in ARGB_8888 format
     * @param width Width of the image in pixels
     * @param height Height of the image in pixels
     * @return [PoseResult] with detected keypoints
     */
    suspend operator fun invoke(
        imageBytes: ByteArray,
        width: Int,
        height: Int
    ): PoseResult {
        return poseDetector.detect(imageBytes, width, height)
    }
}
