package com.example.calitech.di

import com.example.calitech.domain.usecase.PoseEstimationUseCase
import com.example.calitech.ui.viewmodel.CameraViewModel
import com.example.calitech.ui.viewmodel.MediaViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Common Koin module — wires shared dependencies across all platforms.
 *
 * Platform-specific bindings (PoseDetector, MediaPicker) are provided
 * by the respective [platformModule] from each source set.
 */
val commonModule = module {
    // Use Cases
    factoryOf(::PoseEstimationUseCase)

    // ViewModels
    viewModelOf(::CameraViewModel)
    viewModelOf(::MediaViewModel)
}
