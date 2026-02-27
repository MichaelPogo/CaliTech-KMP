package com.example.calitech.platform

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * iOS implementation of [MediaPicker].
 *
 * Will use UIImagePickerController or PHPickerViewController for media selection.
 * Full implementation requires iOS-side integration.
 */
actual class MediaPicker {

    actual suspend fun pickImage(): CameraFrame? {
        // TODO: Implement using UIImagePickerController via Kotlin/Native interop
        return null
    }

    actual suspend fun pickVideo(): Flow<CameraFrame>? {
        // TODO: Implement video picker and frame extraction
        return emptyFlow()
    }
}
