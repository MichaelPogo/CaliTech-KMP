package com.example.calitech

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.example.calitech.platform.MediaPicker
import com.example.calitech.ui.screen.MediaScreen
import com.example.calitech.ui.viewmodel.MediaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import javax.imageio.ImageIO

/**
 * Desktop/JVM actual implementation for the media screen.
 *
 * Uses AWT [FileDialog] for file picking and [ImageIO] for decoding.
 */
@Composable
actual fun PlatformMediaScreen(
    viewModel: MediaViewModel,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    MediaScreen(
        viewModel = viewModel,
        onBack = onBack,
        onPickImage = {
            scope.launch {
                val file = withContext(Dispatchers.IO) {
                    val dialog = FileDialog(Frame(), "Select Image", FileDialog.LOAD)
                    dialog.setFilenameFilter { _, name ->
                        name.lowercase().let {
                            it.endsWith(".png") || it.endsWith(".jpg") ||
                                    it.endsWith(".jpeg") || it.endsWith(".bmp")
                        }
                    }
                    dialog.isVisible = true
                    if (dialog.file != null) {
                        File(dialog.directory, dialog.file)
                    } else null
                }

                if (file != null) {
                    viewModel.setLoading()
                    val frame = MediaPicker.decodeImageFile(file)
                    if (frame != null) {
                        // Create ImageBitmap for display
                        withContext(Dispatchers.IO) {
                            val bufferedImage = ImageIO.read(file)
                            imageBitmap = bufferedImage?.toComposeImageBitmap()
                        }
                        viewModel.onImageSelected(frame)
                    }
                }
            }
        },
        imageBitmap = imageBitmap
    )
}
