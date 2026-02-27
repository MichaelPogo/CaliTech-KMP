package com.example.calitech.di

import com.example.calitech.domain.detector.AndroidPoseDetector
import com.example.calitech.domain.detector.PoseDetector
import com.example.calitech.platform.MediaPicker
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android-specific Koin module.
 *
 * Provides platform bindings for:
 * - [PoseDetector] → [AndroidPoseDetector] (TFLite on Android)
 * - [MediaPicker] → Android implementation with content resolver
 */
actual fun platformModule(): Module = module {
    single<PoseDetector> { AndroidPoseDetector(get()) }
    single { MediaPicker(get()) }
}
