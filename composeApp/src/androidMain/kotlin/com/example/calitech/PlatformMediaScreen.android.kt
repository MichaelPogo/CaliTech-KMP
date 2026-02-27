package com.example.calitech

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.example.calitech.platform.MediaPicker
import com.example.calitech.ui.screen.MediaScreen
import com.example.calitech.ui.viewmodel.MediaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Android actual implementation for the media screen.
 *
 * Uses the Android Photo Picker (Activity Result API) to select images,
 * decodes them, runs pose detection, and displays with skeleton overlay.
 */
@Composable
actual fun PlatformMediaScreen(
    viewModel: MediaViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val mediaPicker = remember { MediaPicker(context) }
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setLoading()
            scope.launch {
                // Decode image
                val frame = mediaPicker.decodeImageUri(uri)
                if (frame != null) {
                    // Create ImageBitmap for display
                    withContext(Dispatchers.IO) {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream?.close()
                        imageBitmap = bitmap?.asImageBitmap()
                    }
                    // Run pose detection
                    viewModel.onImageSelected(frame)
                }
            }
        }
    }

    MediaScreen(
        viewModel = viewModel,
        onBack = onBack,
        onPickImage = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(
                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        },
        imageBitmap = imageBitmap
    )
}
