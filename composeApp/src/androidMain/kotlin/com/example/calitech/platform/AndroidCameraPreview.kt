package com.example.calitech.platform

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.nio.ByteBuffer
import java.util.concurrent.Executors

/**
 * Android-specific camera preview composable using CameraX.
 *
 * Integrates CameraX [PreviewView] with Compose via [AndroidView],
 * and attaches an [ImageAnalysis] use case to capture frames for
 * pose detection inference.
 *
 * @param isFrontCamera Whether to use the front-facing camera
 * @param onFrameCaptured Callback with each analyzed frame as [CameraFrame]
 */
@Composable
fun AndroidCameraPreview(
    isFrontCamera: Boolean,
    onFrameCaptured: (CameraFrame) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            executor.shutdown()
        }
    }

    LaunchedEffect(isFrontCamera) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(executor) { imageProxy ->
                        val frame = imageProxyToCameraFrame(imageProxy)
                        if (frame != null) {
                            onFrameCaptured(frame)
                        }
                        imageProxy.close()
                    }
                }

            val cameraSelector = if (isFrontCamera) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier.fillMaxSize()
    )
}

/**
 * Convert a CameraX [ImageProxy] (RGBA_8888) to our common [CameraFrame].
 * Applies rotation handling so the frame is upright for the pose model.
 */
private fun imageProxyToCameraFrame(imageProxy: ImageProxy): CameraFrame? {
    return try {
        val plane = imageProxy.planes[0]
        val buffer = plane.buffer
        val rowStride = plane.rowStride
        val pixelStride = plane.pixelStride
        val width = imageProxy.width
        val height = imageProxy.height
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees

        // Copy RGBA buffer to byte array
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        // Convert RGBA to ARGB
        val argbBytes = ByteArray(width * height * 4)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val srcIdx = y * rowStride + x * pixelStride
                val dstIdx = (y * width + x) * 4

                if (srcIdx + 3 < bytes.size && dstIdx + 3 < argbBytes.size) {
                    argbBytes[dstIdx] = bytes[srcIdx + 3]     // A (from RGBA alpha)
                    argbBytes[dstIdx + 1] = bytes[srcIdx]     // R
                    argbBytes[dstIdx + 2] = bytes[srcIdx + 1] // G
                    argbBytes[dstIdx + 3] = bytes[srcIdx + 2] // B
                }
            }
        }

        // Apply rotation if needed
        var finalBytes = argbBytes
        var finalWidth = width
        var finalHeight = height

        if (rotationDegrees != 0) {
            val rotated = ByteArray(argbBytes.size)
            finalWidth = if (rotationDegrees == 90 || rotationDegrees == 270) height else width
            finalHeight = if (rotationDegrees == 90 || rotationDegrees == 270) width else height

            for (y in 0 until height) {
                for (x in 0 until width) {
                    val srcIdx = (y * width + x) * 4
                    var dstX = x
                    var dstY = y

                    when (rotationDegrees) {
                        90 -> {
                            dstX = height - 1 - y
                            dstY = x
                        }
                        180 -> {
                            dstX = width - 1 - x
                            dstY = height - 1 - y
                        }
                        270 -> {
                            dstX = y
                            dstY = width - 1 - x
                        }
                    }

                    val dstIdx = (dstY * finalWidth + dstX) * 4
                    rotated[dstIdx] = argbBytes[srcIdx]
                    rotated[dstIdx + 1] = argbBytes[srcIdx + 1]
                    rotated[dstIdx + 2] = argbBytes[srcIdx + 2]
                    rotated[dstIdx + 3] = argbBytes[srcIdx + 3]
                }
            }
            finalBytes = rotated
        }

        CameraFrame(
            data = finalBytes,
            width = finalWidth,
            height = finalHeight,
            timestampMs = System.currentTimeMillis()
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
