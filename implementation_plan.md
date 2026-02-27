# CaliTech Implementation Plan — TFLite MoveNet Pose Detection

## Background

The existing project is a standard KMP Compose Multiplatform template (Kotlin 2.3.0, Compose 1.10.0) with a `composeApp` module containing `commonMain`, `androidMain`, `iosMain`, `jvmMain` source sets, and template boilerplate (`App.kt`, `Platform.kt`, etc.). No Koin, Ktor, TFLite, navigation, or camera dependencies currently exist.

We will build the app per the approved SPEC: a two-button home screen with **Live Camera** and **Open Media** flows, both sending frames through TFLite MoveNet for skeleton overlay rendering.

---

## User Review Required

> [!IMPORTANT]
> **TFLite Model Asset:** The `.tflite` MoveNet model file must be bundled in the app. I will create a placeholder path (`composeApp/src/commonMain/composeResources/files/movenet_lightning.tflite`) — you'll need to download the model file from [TensorFlow Hub](https://tfhub.dev/google/lite-model/movenet/singlepose/lightning/tflite/int8/4) and place it there. I cannot download binary files automatically.

> [!IMPORTANT]
> **iOS TFLite SPM:** The iOS actual implementation references TensorFlow Lite via Swift Package Manager. You will need to add the TFLite Swift SPM package (`https://github.com/nicklama/tensorflow-lite-swift`) to your Xcode project manually for the iOS target to compile.

> [!WARNING]
> **Desktop TFLite:** TFLite desktop/JVM support is limited. The implementation will use the `org.tensorflow:tensorflow-lite` JVM artifact, which includes native binaries for macOS/Linux/Windows. Performance on desktop may differ from mobile.

> [!IMPORTANT]
> **Scope of this implementation:** This plan covers the full project scaffolding — dependency setup, domain layer, platform TFLite implementations, Koin DI, navigation, and all three screens (Home, Camera, Media). The Camera and Media screens will have the real-time skeleton overlay pipeline fully wired. The actual frame capture pipeline on iOS and Desktop uses `expect`/`actual` stubs that are architecturally correct but will need platform-specific runtime testing on those devices.

---

## Proposed Changes

### 1. Dependencies & Build Configuration

#### [MODIFY] [libs.versions.toml](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/gradle/libs.versions.toml)
Add version entries and library/plugin declarations for:
- **Koin** (koin-core, koin-compose, koin-android)
- **Ktor** (ktor-client-core, ktor-client-okhttp, ktor-client-darwin)
- **Compose Navigation** (navigation-compose)
- **CameraX** (camera-core, camera-camera2, camera-lifecycle, camera-view)
- **TFLite** (tensorflow-lite, tensorflow-lite-gpu, tensorflow-lite-support)
- **KotlinX Coroutines** (already present for swing, add core)
- **Accompanist Permissions** (for Android runtime permission handling)

#### [MODIFY] [build.gradle.kts](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/build.gradle.kts)
Add dependencies per source set:
- `commonMain`: koin-core, koin-compose, navigation-compose, kotlinx-coroutines-core
- `androidMain`: koin-android, camerax, tensorflow-lite, tensorflow-lite-support, accompanist-permissions
- `iosMain`: (no Gradle deps — TFLite via SPM in Xcode)
- `jvmMain`: tensorflow-lite JVM, webcam-capture

#### [MODIFY] [AndroidManifest.xml](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/androidMain/AndroidManifest.xml)
Add `<uses-permission>` for `CAMERA` and `READ_MEDIA_IMAGES` / `READ_MEDIA_VIDEO`.

---

### 2. Core Domain Layer (`commonMain`)

#### [NEW] [Keypoint.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/commonMain/kotlin/com/example/calitech/domain/model/Keypoint.kt)
Data classes: `Keypoint(bodyPart, x, y, confidence)`, `BodyPart` enum (17 COCO keypoints), `PoseResult(keypoints, score)`.

#### [NEW] [SkeletonConnections.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/commonMain/kotlin/com/example/calitech/domain/model/SkeletonConnections.kt)
A list of `Pair<BodyPart, BodyPart>` defining which keypoints connect to draw skeleton lines.

#### [NEW] [PoseDetector.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/commonMain/kotlin/com/example/calitech/domain/detector/PoseDetector.kt)
`expect` class / interface with `suspend fun detect(imageData: ByteArray, width: Int, height: Int): PoseResult`.

#### [NEW] [PoseEstimationUseCase.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/commonMain/kotlin/com/example/calitech/domain/usecase/PoseEstimationUseCase.kt)
Orchestrates calling `PoseDetector.detect()` and returning the result. Follows Single Responsibility.

---

### 3. Platform TFLite Implementations

#### [NEW] [PoseDetector.android.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/androidMain/kotlin/com/example/calitech/domain/detector/PoseDetector.android.kt)
`actual` implementation using Android TFLite `Interpreter` — loads `movenet_lightning.tflite` from assets, pre-processes input, runs inference, post-processes 17 keypoints.

#### [NEW] [PoseDetector.ios.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/iosMain/kotlin/com/example/calitech/domain/detector/PoseDetector.ios.kt)
`actual` stub for iOS. The real TFLite inference will be bridged from a Swift helper class (via SPM TFLite package). For now, will return placeholder keypoints until the SPM integration is wired in Xcode.

#### [NEW] [PoseDetector.jvm.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/jvmMain/kotlin/com/example/calitech/domain/detector/PoseDetector.jvm.kt)
`actual` implementation using `org.tensorflow:tensorflow-lite` JVM artifact to load the model and run inference.

---

### 4. Camera Implementations

#### [NEW] [CameraFrameProvider.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/commonMain/kotlin/com/example/calitech/platform/CameraFrameProvider.kt)
`expect` class that emits camera frames via `Flow<CameraFrame>`.

#### [NEW] [CameraFrameProvider.android.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/androidMain/kotlin/com/example/calitech/platform/CameraFrameProvider.android.kt)
`actual` using CameraX `ImageAnalysis` use case, converting `ImageProxy` to `CameraFrame` bytes.

#### [NEW] [CameraPreviewView.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/androidMain/kotlin/com/example/calitech/platform/CameraPreviewView.kt)
Android Compose `AndroidView` wrapping a CameraX `PreviewView`.

#### [NEW] [CameraFrameProvider.ios.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/iosMain/kotlin/com/example/calitech/platform/CameraFrameProvider.ios.kt)
`actual` stub using `AVCaptureSession` via Kotlin/Native interop.

#### [NEW] [CameraFrameProvider.jvm.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/jvmMain/kotlin/com/example/calitech/platform/CameraFrameProvider.jvm.kt)
`actual` using webcam-capture or OpenCV for desktop.

---

### 5. Koin DI

#### [NEW] [CommonModule.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/commonMain/kotlin/com/example/calitech/di/CommonModule.kt)
Common Koin module wiring `PoseEstimationUseCase` and ViewModels.

#### [NEW] [PlatformModule.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/commonMain/kotlin/com/example/calitech/di/PlatformModule.kt)
`expect fun platformModule(): Module` — each platform provides platform-specific bindings (PoseDetector, CameraFrameProvider).

#### [NEW] [PlatformModule.android.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/androidMain/kotlin/com/example/calitech/di/PlatformModule.android.kt)
`actual` Koin module providing `AndroidPoseDetector`, `AndroidCameraFrameProvider`.

#### [NEW] [PlatformModule.ios.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/iosMain/kotlin/com/example/calitech/di/PlatformModule.ios.kt)
`actual` Koin module providing iOS implementations.

#### [NEW] [PlatformModule.jvm.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/jvmMain/kotlin/com/example/calitech/di/PlatformModule.jvm.kt)
`actual` Koin module providing JVM implementations.

---

### 6. ViewModels (commonMain)

#### [NEW] [CameraViewModel.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/commonMain/kotlin/com/example/calitech/ui/viewmodel/CameraViewModel.kt)
Manages `StateFlow<CameraUiState>` — collects camera frames, runs pose estimation, exposes `PoseResult` for overlay.

#### [NEW] [MediaViewModel.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/commonMain/kotlin/com/example/calitech/ui/viewmodel/MediaViewModel.kt)
Manages `StateFlow<MediaUiState>` — loads image/video, runs pose estimation, exposes result for overlay.

---

### 7. UI Screens (commonMain — Compose Multiplatform)

#### [MODIFY] [App.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/commonMain/kotlin/com/example/calitech/App.kt)
Replace template boilerplate with Koin initialization + NavHost with 3 routes: Home, Camera, Media.

#### [NEW] [HomeScreen.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/commonMain/kotlin/com/example/calitech/ui/screen/HomeScreen.kt)
Two prominent buttons: "Open Camera" and "Open Media", plus app title.

#### [NEW] [CameraScreen.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/commonMain/kotlin/com/example/calitech/ui/screen/CameraScreen.kt)
Full-screen camera preview with skeleton overlay Canvas, back button, camera flip.

#### [NEW] [MediaScreen.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/commonMain/kotlin/com/example/calitech/ui/screen/MediaScreen.kt)
Displays selected image/video with skeleton overlay, media picker integration.

#### [NEW] [SkeletonOverlay.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/commonMain/kotlin/com/example/calitech/ui/components/SkeletonOverlay.kt)
Reusable Composable using `Canvas` to draw keypoint dots and skeleton line connections.

#### [NEW] [AppTheme.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/commonMain/kotlin/com/example/calitech/ui/theme/AppTheme.kt)
Dark-themed Material3 theme with custom colors.

#### [NEW] [Navigation.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/commonMain/kotlin/com/example/calitech/ui/navigation/Navigation.kt)
Sealed class for routes + `NavHost` setup.

---

### 8. Entry Point Updates

#### [MODIFY] [MainActivity.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/androidMain/kotlin/com/example/calitech/MainActivity.kt)
Start Koin in `Application` class or `onCreate` before `setContent`.

#### [MODIFY] [MainViewController.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/iosMain/kotlin/com/example/calitech/MainViewController.kt)
Initialize Koin before returning ComposeUIViewController.

#### [MODIFY] [main.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/jvmMain/kotlin/com/example/calitech/main.kt)
Initialize Koin before the Compose window.

---

### 9. Cleanup

#### [DELETE] [Greeting.kt](file:///c:/Users/micha/AndroidStudioProjects/CaliTech/composeApp/src/commonMain/kotlin/com/example/calitech/Greeting.kt)
Template boilerplate, no longer needed.

---

## Verification Plan

### Automated Tests
Due to the heavy platform-native nature (camera, TFLite), the primary verification is compilation:

```bash
# Windows — verify all platforms compile
.\gradlew.bat compileKotlinAndroid
.\gradlew.bat compileKotlinIosArm64
.\gradlew.bat compileKotlinIosSimulatorArm64
.\gradlew.bat compileKotlinJvm
```

### Manual Verification
1. **Android:** Open the project in Android Studio → Run on device/emulator → verify Home screen with two buttons → tap "Open Camera" → grant permission → verify camera preview with skeleton overlay appears → go back → tap "Open Media" → select an image → verify skeleton overlay.
2. **iOS:** Open `iosApp.xcodeproj` in Xcode → add TFLite SPM → build and run on simulator → same flow as Android.
3. **Desktop:** Run `.\gradlew.bat :composeApp:run` → verify window opens with Home screen → test buttons.
