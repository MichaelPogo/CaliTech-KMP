package com.example.calitech.domain.detector

import com.example.calitech.domain.model.BodyPart
import com.example.calitech.domain.model.Keypoint
import com.example.calitech.domain.model.PoseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Desktop/JVM implementation of [PoseDetector].
 *
 * Uses TensorFlow Lite Java API for inference on desktop platforms.
 * Loads the MoveNet Thunder model from the classpath resources.
 *
 * Note: TFLite JVM support may have limited GPU acceleration on desktop.
 */
class JvmPoseDetector : PoseDetector {

    companion object {
        private const val MODEL_INPUT_SIZE = 256
        private const val NUM_KEYPOINTS = 17
        private const val MODEL_RESOURCE_PATH = "/files/move_net_thunder4.tflite"
    }

    private var isInitialized = false

    init {
        initializeInterpreter()
    }

    private fun initializeInterpreter() {
        // Check if model exists in classpath
        val resource = this::class.java.getResourceAsStream(MODEL_RESOURCE_PATH)
        if (resource != null) {
            resource.close()
            isInitialized = true
        }
    }

    override suspend fun detect(
        imageBytes: ByteArray,
        width: Int,
        height: Int
    ): PoseResult = withContext(Dispatchers.Default) {
        if (!isInitialized) {
            return@withContext PoseResult.EMPTY
        }

        // Pre-process
        val inputData = preprocessImage(imageBytes, width, height)

        // TODO: Wire up org.tensorflow:tensorflow-lite JVM interpreter
        // when the JVM TFLite dependency is fully configured.
        // For now, return empty result as desktop TFLite needs native binaries.

        PoseResult.EMPTY
    }

    /**
     * Resize and normalize input image bytes for MoveNet Thunder model.
     */
    private fun preprocessImage(imageBytes: ByteArray, width: Int, height: Int): FloatArray {
        val inputSize = MODEL_INPUT_SIZE * MODEL_INPUT_SIZE * 3
        val result = FloatArray(inputSize)

        for (y in 0 until MODEL_INPUT_SIZE) {
            for (x in 0 until MODEL_INPUT_SIZE) {
                val srcX = (x * width) / MODEL_INPUT_SIZE
                val srcY = (y * height) / MODEL_INPUT_SIZE
                val pixelIndex = (srcY * width + srcX) * 4

                if (pixelIndex + 3 < imageBytes.size) {
                    val outIndex = (y * MODEL_INPUT_SIZE + x) * 3
                    result[outIndex] = (imageBytes[pixelIndex + 1].toInt() and 0xFF).toFloat()
                    result[outIndex + 1] = (imageBytes[pixelIndex + 2].toInt() and 0xFF).toFloat()
                    result[outIndex + 2] = (imageBytes[pixelIndex + 3].toInt() and 0xFF).toFloat()
                }
            }
        }

        return result
    }

    override fun close() {
        isInitialized = false
    }
}
