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
 * Represents the type of media loaded.
 */
enum class MediaType {
    NONE, IMAGE, VIDEO
}

/**
 * UI State for the Media analysis screen.
 */
data class MediaUiState(
    val isLoading: Boolean = false,
    val isDetecting: Boolean = false,
    val mediaType: MediaType = MediaType.NONE,
    val imageFrame: CameraFrame? = null,
    val poseResult: PoseResult = PoseResult.EMPTY,
    val errorMessage: String? = null
)

/**
 * ViewModel for the Media Analysis screen.
 *
 * Follows MVVM — manages state for image/video selection and
 * pose detection on the selected media.
 *
 * @property poseEstimationUseCase Injected use case for running pose detection
 */
class MediaViewModel(
    private val poseEstimationUseCase: PoseEstimationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MediaUiState())
    val uiState: StateFlow<MediaUiState> = _uiState.asStateFlow()

    /**
     * Process an image frame through MoveNet.
     * Called after the user picks an image from the gallery.
     */
    fun onImageSelected(frame: CameraFrame) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isDetecting = true,
                    mediaType = MediaType.IMAGE,
                    imageFrame = frame
                )
            }

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
     * Reset the screen state when the user wants to pick another media.
     */
    fun resetState() {
        _uiState.update { MediaUiState() }
    }

    /**
     * Set loading state when picking media.
     */
    fun setLoading() {
        _uiState.update { it.copy(isLoading = true) }
    }

    /**
     * Clear any error state.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
