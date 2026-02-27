package com.example.calitech.di

import org.koin.core.module.Module

/**
 * Expected function to provide platform-specific Koin module.
 *
 * Each platform (Android, iOS, JVM) implements this to provide
 * bindings for [PoseDetector], [MediaPicker], and other
 * platform-specific dependencies.
 */
expect fun platformModule(): Module
