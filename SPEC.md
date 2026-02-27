# CaliTech — Application Specification

## 1. Overview

CaliTech is a **Kotlin Multiplatform (KMP)** application targeting **Android, iOS, and Desktop** that performs **real-time human body pose estimation** using **TensorFlow Lite** with the **MoveNet** model. The app detects body joints (keypoints) and renders them as an overlay on live camera feed, videos, and images.

---

## 2. Target Platforms

| Platform | UI Framework         | TFLite Integration          |
|----------|----------------------|-----------------------------|
| Android  | Compose Multiplatform | TFLite Android Support API |
| iOS      | Compose Multiplatform | TFLite iOS via SPM          |
| Desktop  | Compose Multiplatform | TFLite JVM / native interop |

---

## 3. Core Features

### 3.1 Live Camera Pose Detection
- A **"Open Camera"** button on the home screen launches a full-screen camera view.
- The camera feed is processed frame-by-frame through TensorFlow Lite running the **MoveNet (SinglePose Lightning or Thunder)** model.
- Detected **17 keypoints** (nose, eyes, ears, shoulders, elbows, wrists, hips, knees, ankles) are drawn as dots on the live preview.
- Skeletal lines connecting the keypoints are rendered in real-time over the camera feed.
- Users can toggle between front and back cameras.

### 3.2 Image / Video Pose Detection
- A **"Open Media"** button on the home screen opens the device's file/gallery picker.
- The user can select either a **picture** or a **video**.
- **Image mode:** The selected image is displayed with keypoints and skeleton overlay drawn on top after inference.
- **Video mode:** The video is played back with keypoints and skeleton overlay rendered on each frame in real-time.

---

## 4. MoveNet Model Details

| Property         | Value                                          |
|------------------|-------------------------------------------------|
| Model            | MoveNet (SinglePose Lightning / Thunder variant) |
| Input            | 192×192 (Lightning) or 256×256 (Thunder) RGB    |
| Output           | 17 keypoints, each with (x, y, confidence)      |
| Format           | `.tflite` (bundled as an app asset)             |
| Inference Engine | TensorFlow Lite                                 |

### 4.1 Keypoint Map (COCO 17)
```
0: Nose
1: Left Eye       2: Right Eye
3: Left Ear       4: Right Ear
5: Left Shoulder  6: Right Shoulder
7: Left Elbow     8: Right Elbow
9: Left Wrist     10: Right Wrist
11: Left Hip      12: Right Hip
13: Left Knee     14: Right Knee
15: Left Ankle    16: Right Ankle
```

---

## 5. Architecture & Design

### 5.1 MVVM Layers

```
┌──────────────────────────────────┐
│            View (Compose)        │  Screens, Overlays, Camera Preview
├──────────────────────────────────┤
│          ViewModel               │  StateFlow, UI state, user actions
├──────────────────────────────────┤
│       Use Cases / Interactors    │  RunPoseEstimation, ProcessMedia
├──────────────────────────────────┤
│        Repository / Data         │  ModelLoader, FrameProcessor
├──────────────────────────────────┤
│    Platform (expect/actual)      │  Camera, Gallery, TFLite engine
└──────────────────────────────────┘
```

### 5.2 Key Design Patterns
- **Strategy Pattern** — Swap between MoveNet Lightning vs. Thunder model at runtime.
- **Factory Pattern** — Create the correct platform-specific TFLite interpreter.
- **Observer Pattern** — UI observes pose detection results via `StateFlow`.
- **Adapter Pattern** — Normalize platform camera frame formats to a common input for inference.

### 5.3 KMP Module Structure (Proposed)

```
:shared             → Common code (ViewModels, Use Cases, model interfaces)
:shared:data        → Repository, model loading, frame processing abstractions
:composeApp         → Compose Multiplatform UI (screens, overlays, navigation)
:androidApp         → Android entry point, platform DI
:iosApp             → iOS entry point (SwiftUI host)
:desktopApp         → Desktop entry point
```

---

## 6. Technology Stack

| Concern               | Library / Tool                        |
|------------------------|---------------------------------------|
| Language               | Kotlin (+ Swift for iOS entry)        |
| UI                     | Compose Multiplatform                 |
| Networking             | Ktor (if remote model download needed)|
| Dependency Injection   | Koin                                  |
| ML Inference           | TensorFlow Lite                       |
| Camera (Android)       | CameraX                              |
| Camera (iOS)           | AVFoundation (via expect/actual)      |
| Camera (Desktop)       | OpenCV / Webcam Capture               |
| Image/Video Picker     | Platform file/gallery pickers         |
| Navigation             | Compose Multiplatform Navigation      |
| iOS Dependency Mgmt    | Swift Package Manager (SPM)           |

---

## 7. Screens & UI

### 7.1 Home Screen
- App logo / title header.
- **"Open Camera"** button — navigates to the Live Camera screen.
- **"Open Media"** button — opens the native file/gallery picker for images or videos.

### 7.2 Live Camera Screen
- Full-screen camera preview with the skeleton overlay rendered on top.
- Floating controls: switch camera (front/back), close/back button.
- Confidence threshold indicator (optional).

### 7.3 Media Analysis Screen
- Displays the chosen image or video with the pose skeleton overlay.
- For video: standard playback controls (play, pause, seek).
- For image: pinch-to-zoom with overlay intact.

---

## 8. Data Flow

```
Camera Frame / Media Frame
        │
        ▼
  Pre-processing (resize, normalize to model input)
        │
        ▼
  TFLite Interpreter (MoveNet inference)
        │
        ▼
  Post-processing (extract 17 keypoints + confidence)
        │
        ▼
  ViewModel (StateFlow<PoseResult>)
        │
        ▼
  Compose Canvas Overlay (draw keypoints + skeleton)
```

---

## 9. Platform-Specific Considerations

### Android
- Use **CameraX** for camera access and frame analysis (`ImageAnalysis` use case).
- TFLite via `org.tensorflow:tensorflow-lite` and optional GPU delegate.

### iOS
- Use **AVFoundation** (`AVCaptureSession`) for camera frames.
- TFLite iOS runtime integrated via **SPM** (`tensorflow-lite-swift` package).
- **Do NOT** use CocoaPods.

### Desktop
- Camera access via **OpenCV** or **Webcam Capture** library.
- TFLite via the JVM TFLite bindings or native interop.

---

## 10. Non-Functional Requirements

| Requirement   | Target                                            |
|---------------|---------------------------------------------------|
| Frame Rate    | ≥ 15 FPS inference on mid-range devices           |
| Model Size    | ≤ 10 MB (bundled `.tflite` asset)                 |
| Permissions   | Camera, Storage/Media (runtime request)           |
| Min Android   | API 26 (Android 8.0)                              |
| Min iOS       | iOS 16+                                           |
| Accessibility | Content descriptions on all interactive elements  |

---

## 11. Future Considerations (Out of Scope for v1)
- Multi-pose detection (detecting multiple people simultaneously).
- Exercise counting and rep tracking based on joint angles.
- Recording and exporting annotated video with skeleton overlay.
- Cloud-based model updates via Ktor.
