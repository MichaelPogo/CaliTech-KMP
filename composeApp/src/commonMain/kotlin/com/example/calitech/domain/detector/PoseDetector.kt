package com.example.calitech.domain.detector

import com.example.calitech.domain.model.PoseResult

/**
 * Platform-agnostic pose detector interface.
 *
 * Each platform provides its own implementation backed by TensorFlow Lite
 * running the MoveNet Thunder model.
 *
 * Follows the Strategy pattern — the concrete implementation is selected
 * at runtime via dependency injection (Koin).
 */
interface PoseDetector {

    /**
     * Run pose estimation on the given image data.
     *
     * @param imageBytes Raw pixel data in ARGB_8888 format (4 bytes per pixel)
     * @param width Width of the source image in pixels
     * @param height Height of the source image in pixels
     * @return A [PoseResult] containing 17 detected keypoints with confidence scores
     */
    suspend fun detect(imageBytes: ByteArray, width: Int, height: Int): PoseResult

    /**
     * Release resources held by the interpreter.
     */
    fun close()
}
