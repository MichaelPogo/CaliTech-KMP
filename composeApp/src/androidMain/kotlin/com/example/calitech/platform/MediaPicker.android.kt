package com.example.calitech.platform

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

/**
 * Android implementation of [MediaPicker].
 *
 * Uses Android content resolver to decode images and videos
 * from Uris provided by the Activity Result API photo picker.
 */
actual class MediaPicker(
    private val context: Context
) {
    /**
     * Decode an image Uri into a CameraFrame.
     * This is called after the user picks an image via the photo picker.
     */
    actual suspend fun pickImage(): CameraFrame? {
        // This method will be called with the URI from the activity result
        // The actual URI selection is handled by the UI layer
        return null
    }

    /**
     * Decode a video Uri into a flow of frames.
     * This is called after the user picks a video.
     */
    actual suspend fun pickVideo(): Flow<CameraFrame>? {
        return emptyFlow()
    }

    /**
     * Decode a content URI to a CameraFrame.
     * Called by the ViewModel after the user selects media.
     */
    suspend fun decodeImageUri(uri: Uri): CameraFrame? = withContext(Dispatchers.IO) {
        try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.isMutableRequired = true
                }
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }

            bitmapToCameraFrame(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        /**
         * Convert a Bitmap to CameraFrame (ARGB_8888 byte array).
         */
        fun bitmapToCameraFrame(bitmap: Bitmap): CameraFrame {
            val config = Bitmap.Config.ARGB_8888
            val convertedBitmap = if (bitmap.config != config) {
                bitmap.copy(config, false)
            } else {
                bitmap
            }

            val width = convertedBitmap.width
            val height = convertedBitmap.height
            val byteBuffer = ByteBuffer.allocate(width * height * 4)
            convertedBitmap.copyPixelsToBuffer(byteBuffer)

            return CameraFrame(
                data = byteBuffer.array(),
                width = width,
                height = height,
                timestampMs = System.currentTimeMillis()
            )
        }
    }
}
