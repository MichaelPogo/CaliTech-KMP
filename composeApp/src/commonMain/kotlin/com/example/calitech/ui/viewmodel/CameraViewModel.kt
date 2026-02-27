package com.example.calitech.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calitech.domain.model.PoseResult
import com.example.calitech.domain.usecase.PoseEstimationUseCase
import com.example.calitech.platform.CameraFrame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for the Camera screen.
 */
data class CameraUiState(
    val isDetecting: Boolean = false,
    val poseResult: PoseResult = PoseResult.EMPTY,
    val isFrontCamera: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel for the Live Camera screen.
 *
 * Follows MVVM — exposes reactive [StateFlow] for the UI to observe.
 * Uses the Observer pattern via StateFlow for reactive updates.
 *
 * @property poseEstimationUseCase Injected use case for running pose detection
 */
class CameraViewModel(
    private val poseEstimationUseCase: PoseEstimationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    /**
     * Process a single camera frame through MoveNet.
     * Called by the camera preview for each analysis frame.
     */
    fun onFrameCaptured(frame: CameraFrame) {
        // Skip if already processing to avoid backpressure
        if (_uiState.value.isDetecting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isDetecting = true) }

            try {
                val result = poseEstimationUseCase(
                    imageBytes = frame.data,
                    width = frame.width,
                    height = frame.height
                )
                _uiState.update {
                    it.copy(
                        isDetecting = false,
                        poseResult = result,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDetecting = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    /**
     * Toggle between front and back cameras.
     */
    fun toggleCamera() {
        _uiState.update { it.copy(isFrontCamera = !it.isFrontCamera) }
    }

    /**
     * Clear any error state.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
