package com.example.calitech.domain.model

/**
 * Represents a single body part keypoint detected by MoveNet.
 * Uses the COCO 17-keypoint topology.
 */
enum class BodyPart(val index: Int) {
    NOSE(0),
    LEFT_EYE(1),
    RIGHT_EYE(2),
    LEFT_EAR(3),
    RIGHT_EAR(4),
    LEFT_SHOULDER(5),
    RIGHT_SHOULDER(6),
    LEFT_ELBOW(7),
    RIGHT_ELBOW(8),
    LEFT_WRIST(9),
    RIGHT_WRIST(10),
    LEFT_HIP(11),
    RIGHT_HIP(12),
    LEFT_KNEE(13),
    RIGHT_KNEE(14),
    LEFT_ANKLE(15),
    RIGHT_ANKLE(16);

    companion object {
        fun fromIndex(index: Int): BodyPart =
            entries.first { it.index == index }
    }
}

/**
 * A single keypoint with normalized coordinates (0..1) and a confidence score.
 */
data class Keypoint(
    val bodyPart: BodyPart,
    val x: Float,
    val y: Float,
    val confidence: Float
)

/**
 * The result of a single-pose MoveNet inference.
 *
 * @property keypoints All 17 detected keypoints
 * @property score The overall confidence of the detection
 */
data class PoseResult(
    val keypoints: List<Keypoint>,
    val score: Float
) {
    companion object {
        val EMPTY = PoseResult(emptyList(), 0f)
    }
}
