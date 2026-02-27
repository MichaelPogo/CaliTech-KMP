package com.example.calitech.di

import com.example.calitech.domain.detector.JvmPoseDetector
import com.example.calitech.domain.detector.PoseDetector
import com.example.calitech.platform.MediaPicker
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * JVM/Desktop-specific Koin module.
 *
 * Provides platform bindings for:
 * - [PoseDetector] → [JvmPoseDetector] (TFLite JVM)
 * - [MediaPicker] → JVM implementation with ImageIO
 */
actual fun platformModule(): Module = module {
    single<PoseDetector> { JvmPoseDetector() }
    single { MediaPicker() }
}
