package com.example.calitech.platform

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import java.awt.image.BufferedImage
import java.io.File
import java.nio.ByteBuffer
import javax.imageio.ImageIO

/**
 * JVM/Desktop implementation of [MediaPicker].
 *
 * Uses Swing JFileChooser for file selection and Java ImageIO for decoding.
 */
actual class MediaPicker {

    actual suspend fun pickImage(): CameraFrame? {
        // On desktop, file picking is handled by the UI layer using JFileChooser
        return null
    }

    actual suspend fun pickVideo(): Flow<CameraFrame>? {
        return emptyFlow()
    }

    companion object {
        /**
         * Decode an image file into a CameraFrame on desktop.
         */
        fun decodeImageFile(file: File): CameraFrame? {
            return try {
                val bufferedImage = ImageIO.read(file) ?: return null
                val argbImage = BufferedImage(
                    bufferedImage.width,
                    bufferedImage.height,
                    BufferedImage.TYPE_INT_ARGB
                )
                argbImage.graphics.drawImage(bufferedImage, 0, 0, null)

                val width = argbImage.width
                val height = argbImage.height
                val pixels = IntArray(width * height)
                argbImage.getRGB(0, 0, width, height, pixels, 0, width)

                val byteBuffer = ByteBuffer.allocate(width * height * 4)
                for (pixel in pixels) {
                    byteBuffer.put(((pixel shr 24) and 0xFF).toByte()) // A
                    byteBuffer.put(((pixel shr 16) and 0xFF).toByte()) // R
                    byteBuffer.put(((pixel shr 8) and 0xFF).toByte())  // G
                    byteBuffer.put((pixel and 0xFF).toByte())          // B
                }

                CameraFrame(
                    data = byteBuffer.array(),
                    width = width,
                    height = height,
                    timestampMs = System.currentTimeMillis()
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
