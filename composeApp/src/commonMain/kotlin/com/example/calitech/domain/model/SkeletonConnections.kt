package com.example.calitech.domain.model

/**
 * Defines the skeleton topology — which keypoints should be connected
 * by lines when rendering the pose overlay.
 */
object SkeletonConnections {

    /**
     * Each pair represents a bone/limb connecting two keypoints.
     */
    val connections: List<Pair<BodyPart, BodyPart>> = listOf(
        // Face
        BodyPart.NOSE to BodyPart.LEFT_EYE,
        BodyPart.NOSE to BodyPart.RIGHT_EYE,
        BodyPart.LEFT_EYE to BodyPart.LEFT_EAR,
        BodyPart.RIGHT_EYE to BodyPart.RIGHT_EAR,

        // Upper body
        BodyPart.LEFT_SHOULDER to BodyPart.RIGHT_SHOULDER,
        BodyPart.LEFT_SHOULDER to BodyPart.LEFT_ELBOW,
        BodyPart.RIGHT_SHOULDER to BodyPart.RIGHT_ELBOW,
        BodyPart.LEFT_ELBOW to BodyPart.LEFT_WRIST,
        BodyPart.RIGHT_ELBOW to BodyPart.RIGHT_WRIST,

        // Torso
        BodyPart.LEFT_SHOULDER to BodyPart.LEFT_HIP,
        BodyPart.RIGHT_SHOULDER to BodyPart.RIGHT_HIP,
        BodyPart.LEFT_HIP to BodyPart.RIGHT_HIP,

        // Lower body
        BodyPart.LEFT_HIP to BodyPart.LEFT_KNEE,
        BodyPart.RIGHT_HIP to BodyPart.RIGHT_KNEE,
        BodyPart.LEFT_KNEE to BodyPart.LEFT_ANKLE,
        BodyPart.RIGHT_KNEE to BodyPart.RIGHT_ANKLE,

        // Nose to shoulders (neck approximation)
        BodyPart.NOSE to BodyPart.LEFT_SHOULDER,
        BodyPart.NOSE to BodyPart.RIGHT_SHOULDER,
    )
}
