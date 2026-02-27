package com.example.calitech.domain.detector

import android.content.Context
import com.example.calitech.domain.model.BodyPart
import com.example.calitech.domain.model.Keypoint
import com.example.calitech.domain.model.PoseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

/**
 * Android implementation of [PoseDetector] using TensorFlow Lite.
 *
 * Loads the MoveNet Thunder model from assets and runs inference
 * to detect 17 body keypoints.
 *
 * Uses the Factory pattern for interpreter creation via companion object.
 */
class AndroidPoseDetector(
    private val context: Context
) : PoseDetector {

    companion object {
        /** MoveNet Thunder input size */
        private const val MODEL_INPUT_SIZE = 256
        private const val MODEL_FILE = "files/move_net_thunder4.tflite"
        private const val NUM_KEYPOINTS = 17
        private const val CONFIDENCE_THRESHOLD = 0.2f
    }

    private val interpreter: Interpreter by lazy {
        val model = loadModelFile()
        val options = Interpreter.Options().apply {
            numThreads = 4
        }
        Interpreter(model, options)
    }

    private fun loadModelFile(): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd(MODEL_FILE)
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    override suspend fun detect(
        imageBytes: ByteArray,
        width: Int,
        height: Int
    ): PoseResult = withContext(Dispatchers.Default) {
        // Pre-process: resize and normalize to model input
        val inputBuffer = preprocessImage(imageBytes, width, height)

        // Output: [1, 1, 17, 3] — 17 keypoints, each with (y, x, confidence)
        val outputArray = Array(1) { Array(1) { Array(NUM_KEYPOINTS) { FloatArray(3) } } }

        // Run inference
        interpreter.run(inputBuffer, outputArray)

        // Post-process: extract keypoints
        postProcessOutput(outputArray)
    }

    /**
     * Resize ARGB_8888 image data to MODEL_INPUT_SIZE and normalize to 0-255 int values
     * in a ByteBuffer suitable for the MoveNet model (input type: uint8 or float32).
     */
    private fun preprocessImage(imageBytes: ByteArray, width: Int, height: Int): ByteBuffer {
        // Allocate 1 byte per channel for uint8/int8 quantized input
        val inputBuffer = ByteBuffer.allocateDirect(1 * MODEL_INPUT_SIZE * MODEL_INPUT_SIZE * 3)
        inputBuffer.order(ByteOrder.nativeOrder())
        inputBuffer.rewind()

        // Simple nearest-neighbor resize from source to MODEL_INPUT_SIZE x MODEL_INPUT_SIZE
        for (y in 0 until MODEL_INPUT_SIZE) {
            for (x in 0 until MODEL_INPUT_SIZE) {
                val srcX = (x * width) / MODEL_INPUT_SIZE
                val srcY = (y * height) / MODEL_INPUT_SIZE
                val pixelIndex = (srcY * width + srcX) * 4 // ARGB_8888

                if (pixelIndex + 3 < imageBytes.size) {
                    // ARGB order — skip alpha, take R, G, B
                    val r = imageBytes[pixelIndex + 1]
                    val g = imageBytes[pixelIndex + 2]
                    val b = imageBytes[pixelIndex + 3]

                    inputBuffer.put(r)
                    inputBuffer.put(g)
                    inputBuffer.put(b)
                } else {
                    inputBuffer.put(0.toByte())
                    inputBuffer.put(0.toByte())
                    inputBuffer.put(0.toByte())
                }
            }
        }

        inputBuffer.rewind()
        return inputBuffer
    }

    /**
     * Convert raw model output [1][1][17][3] to a [PoseResult].
     * Each keypoint has (y, x, confidence) in normalized [0, 1] coordinates.
     */
    private fun postProcessOutput(output: Array<Array<Array<FloatArray>>>): PoseResult {
        val keypoints = mutableListOf<Keypoint>()
        var totalConfidence = 0f

        for (i in 0 until NUM_KEYPOINTS) {
            val ky = output[0][0][i][0] // y normalized
            val kx = output[0][0][i][1] // x normalized
            val confidence = output[0][0][i][2]

            keypoints.add(
                Keypoint(
                    bodyPart = BodyPart.fromIndex(i),
                    x = kx,
                    y = ky,
                    confidence = confidence
                )
            )
            totalConfidence += confidence
        }

        val overallScore = if (keypoints.isNotEmpty()) totalConfidence / keypoints.size else 0f

        return PoseResult(
            keypoints = keypoints,
            score = overallScore
        )
    }

    override fun close() {
        interpreter.close()
    }
}
