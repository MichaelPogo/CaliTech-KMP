package com.example.calitech.platform

/**
 * A platform-agnostic representation of a single camera/media frame.
 *
 * @property data Raw pixel data (ARGB_8888 format, 4 bytes per pixel)
 * @property width Width of the frame in pixels
 * @property height Height of the frame in pixels
 * @property timestampMs Timestamp of the frame capture in milliseconds
 */
data class CameraFrame(
    val data: ByteArray,
    val width: Int,
    val height: Int,
    val timestampMs: Long = 0L
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as CameraFrame
        return width == other.width && height == other.height &&
                timestampMs == other.timestampMs && data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + timestampMs.hashCode()
        return result
    }
}
