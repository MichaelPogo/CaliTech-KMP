package com.example.calitech.di

import com.example.calitech.domain.detector.IosPoseDetector
import com.example.calitech.domain.detector.PoseDetector
import com.example.calitech.platform.MediaPicker
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * iOS-specific Koin module.
 *
 * Provides platform bindings for:
 * - [PoseDetector] → [IosPoseDetector] (TFLite via SPM)
 * - [MediaPicker] → iOS implementation
 */
actual fun platformModule(): Module = module {
    single<PoseDetector> { IosPoseDetector() }
    single { MediaPicker() }
}
