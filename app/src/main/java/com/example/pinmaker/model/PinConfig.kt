package com.example.pinmaker.model

/**
 * Configuration options for PIN generation.
 *
 * @property length Total length of the generated PIN (4 to 12 digits).
 * @property preventRepeatedDigits If true, all digits in the PIN must be unique (0-9 used at most once).
 * @property preventConsecutiveDigits If true, no two adjacent digits can be consecutive (e.g. 1-2, 2-1) or identical (e.g. 1-1).
 * @property prefix Optional custom prefix (must contain only numeric digits [0-9]).
 * @property suffix Optional custom suffix (must contain only numeric digits [0-9]).
 */
data class PinConfig(
    val length: Int = 6,
    val preventRepeatedDigits: Boolean = false,
    val preventConsecutiveDigits: Boolean = false,
    val prefix: String = "",
    val suffix: String = ""
) {
    companion object {
        const val MIN_LENGTH = 4
        const val MAX_LENGTH = 12
        const val DEFAULT_LENGTH = 6
    }
}

sealed class GenerationResult {
    data class Success(val pin: String) : GenerationResult()
    data class Error(val message: String) : GenerationResult()
}
