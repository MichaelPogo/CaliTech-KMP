package com.example.calitech.platform

import kotlinx.coroutines.flow.Flow

/**
 * Abstraction over platform-specific image/media picker.
 *
 * Each platform provides an actual implementation for picking
 * images and videos from the device's gallery/filesystem.
 */
expect class MediaPicker {
    /**
     * Pick an image and return its raw pixel bytes plus dimensions.
     * Returns null if the user cancels.
     */
    suspend fun pickImage(): CameraFrame?

    /**
     * Pick a video and return a flow of its decoded frames.
     * Returns null if the user cancels.
     */
    suspend fun pickVideo(): Flow<CameraFrame>?
}
